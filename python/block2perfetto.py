# This is a sample Python script.

# Press ⌃R to execute it or replace it with your code.
# Press Double ⇧ to search everywhere for classes, files, tool windows, actions, and settings.
import json
import sys
import os
import shutil


def trans(srcJsonFilePath, targetJsonFilePath):
    # 从文件中加载JSON数据
    with open(srcJsonFilePath) as f:
        srcJson = json.load(f)
        stackTraceSamples = srcJson['stackTraceSamples']

        # {"name": "Asub", "cat": "PERF", "ph": "B", "pid": 22630, "tid": 22630, "ts": 100},
        # {"name": "Asub", "cat": "PERF", "ph": "E", "pid": 22630, "tid": 22630, "ts": 200},

        targetJsonArray = []

        # 处理first
        sampleFirst = stackTraceSamples[0]
        timeFirst = sampleFirst['time']
        stackFirst = sampleFirst['stackTraceElements']
        # 栈底在下面，所以翻转一下
        for element in reversed(stackFirst):
            targetJsonArray.append(createTargetElement(element, timeFirst, "B"))

        for index in range(1, len(stackTraceSamples)):
            sample1 = stackTraceSamples[index - 1]
            time1 = sample1['time']
            stack1 = sample1['stackTraceElements']
            # print("stack1:" + str(stack1))
            sample2 = stackTraceSamples[index]
            time2 = sample2['time']
            stack2 = sample2['stackTraceElements']
            # print("stack2:" + str(stack2))
            # 比较两个栈
            diffR = compareStack(stack1, stack2)
            print("找到不同的栈位置（倒数jj）：" + str(diffR))

            diffCount1 = len(stack1) - diffR
            print("diffCount1=" + str(diffCount1))
            diffCount2 = len(stack2) - diffR
            print("diffCount2=" + str(diffCount2))
            # 结束的函数
            for j in range(diffCount1):
                element = stack1[j]
                targetJsonArray.append(createTargetElement(element, time2, "E"))  # 这里结束时间使用time2
            # 开始的函数
            for j in range(diffCount2):
                # 翻转一下
                element = stack2[diffCount2 - 1 - j]
                targetJsonArray.append(createTargetElement(element, time2, "B"))

        # 处理last
        sampleLast = stackTraceSamples[len(stackTraceSamples) - 1]
        timeLast = sampleLast['time']
        stackLast = sampleLast['stackTraceElements']
        for element in stackLast:
            targetJsonArray.append(createTargetElement(element, timeLast, "E"))

        # print(json.dumps(targetJsonArray, indent=2))

        # 将JSON数据保存到文件中
        with open(targetJsonFilePath, 'w') as file:
            json.dump(targetJsonArray, file, indent=4)
        print("JSON数据已保存到文件:" + targetJsonFilePath)


# 相同方法判断：只判断了 类名&方法名
def isSameMethod(element1, element2):
    return element1['declaringClass'] == element2['declaringClass'] and element1['methodName'] == element2[
        'methodName']  # and element1['lineNumber'] == element2['lineNumber']


def compareStack(stack1, stack2):
    result = min(len(stack1), len(stack2))
    for j in range(result):
        j1 = len(stack1) - 1 - j
        j2 = len(stack2) - 1 - j
        element1 = stack1[j1]
        element2 = stack2[j2]
        if not (isSameMethod(element1, element2)):
            print("找到不同的栈位置（倒数j）：" + str(j))
            result = j
            return result
    return result


def createTargetElement(element, time, flag):
    # 使用 split() 函数将字符串根据空格拆分
    parts = element['declaringClass'].split('.')
    # 获取最后一个部分，即类名
    className = parts[-1]
    return {
        "name": className + '#' + element['methodName'],
        "cat": "PERF",
        "ph": flag,  # 开始："B"，结束："E"
        "pid": 22630,  # 假设固定的进程ID
        "tid": 22630,  # 假设固定的线程ID
        "ts": time * 1000
    }


# 使用方法：python3 block2perfetto.py 要转换的采样json文件
if __name__ == '__main__':
    srcJsonFilePath = 'top-915.json'
    if len(sys.argv) > 1:
        srcJsonFilePath = sys.argv[1]
        print('读取到参数：' + sys.argv[1])

    targetJsonFilePath = srcJsonFilePath + '.perfetto'

    trans(srcJsonFilePath, targetJsonFilePath)
