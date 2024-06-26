import json
import sys


def transform(src_json_file, target_json_file):
    with open(src_json_file) as f:
        src_json = json.load(f)
        stack_trace_samples = src_json['stackTraceSamples']

        target_json_array = []

        # 处理第一个 stack
        if stack_trace_samples:
            sample_first = stack_trace_samples[0]
            process_stack(sample_first, target_json_array, "B")

        # 处理中间 stack samples
        for i in range(1, len(stack_trace_samples)):
            sample1 = stack_trace_samples[i - 1]
            sample2 = stack_trace_samples[i]
            compare_and_process(sample1, sample2, target_json_array)

        # 处理最后一个 stack
        if stack_trace_samples:
            sample_last = stack_trace_samples[-1]
            process_stack(sample_last, target_json_array, "E")

        # 将结果写入目标 JSON 文件
        with open(target_json_file, 'w') as file:
            json.dump(target_json_array, file, indent=4)
        print(f"转换完成，结果已保存到文件: {target_json_file}")


def compare_and_process(sample1, sample2, target_json_array):
    stack1 = sample1['stackTraceElements'][::-1]
    stack2 = sample2['stackTraceElements'][::-1]
    diff_index = compare_stack(stack1, stack2)

    time2 = sample2['time']

    # 结束的函数
    for element in stack1[diff_index:]:
        target_json_array.append(create_target_element(element, time2, "E"))

    # 开始的函数
    for element in stack2[diff_index:]:
        target_json_array.append(create_target_element(element, time2, "B"))


def process_stack(sample, target_json_array, flag):
    time = sample['time']
    stack = sample['stackTraceElements'][::-1]  # 反转堆栈顺序

    for element in stack:
        target_json_array.append(create_target_element(element, time, flag))


def compare_stack(stack1, stack2):
    min_length = min(len(stack1), len(stack2))
    for i in range(min_length):
        element1 = stack1[i]
        element2 = stack2[i]
        last_element1 = stack1[i - 1] if i > 0 else None
        last_element2 = stack2[i - 1] if i > 0 else None
        if not is_same_method(element1, element2, last_element1, last_element2):
            return i
    return min_length


def is_same_method(element1, element2, last_element1, last_element2):
    current_element_same = (
            element1['declaringClass'] == element2['declaringClass'] and
            element1['methodName'] == element2['methodName']
    )
    if last_element1 is None and last_element2 is None:
        return current_element_same
    else:
        return current_element_same and last_element1['lineNumber'] == last_element2['lineNumber']


def create_target_element(element, time, flag):
    parts = element['declaringClass'].split('.')
    class_name = parts[-1]
    return {
        "name": f"{class_name}#{element['methodName']}",
        "cat": "PERF",
        "ph": flag,
        "pid": 22630,
        "tid": 22630,
        "ts": time * 1000
    }


# 使用方法：python3 block2perfetto.py 要转换的采样json文件
if __name__ == '__main__':
    src_json = 'error1.json'
    if len(sys.argv) > 1:
        src_json = sys.argv[1]
        print('读取到参数：' + sys.argv[1])

    target_json = src_json + 'V2.perfetto'

    transform(src_json, target_json)
