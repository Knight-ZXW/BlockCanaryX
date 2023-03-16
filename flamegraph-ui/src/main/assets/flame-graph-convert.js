function _defineProperty(obj, key, value) { key = _toPropertyKey(key); if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }
function _toPropertyKey(arg) { var key = _toPrimitive(arg, "string"); return typeof key === "symbol" ? key : String(key); }
function _toPrimitive(input, hint) { if (typeof input !== "object" || input === null) return input; var prim = input[Symbol.toPrimitive]; if (prim !== undefined) { var res = prim.call(input, hint || "default"); if (typeof res !== "object") return res; throw new TypeError("@@toPrimitive must return a primitive value."); } return (hint === "string" ? String : Number)(input); }
/* eslint-disable no-unused-vars */

class Node {
  constructor() {
    _defineProperty(this, "children", null);
    _defineProperty(this, "name", null);
    _defineProperty(this, "value", 0);
  }
  addStack(stackFrames, count) {
    if (stackFrames.length === 0) {
      return;
    }
    if (this.children == null) {
      this.children = [];
    }
    var parentNode = this;
    stackFrames.forEach((stackFrame, index, _) => {
      var node;
      var parentNodeChildren = parentNode.children;
      if (parentNodeChildren != null && parentNodeChildren.length > 0) {
        if (parentNodeChildren[parentNodeChildren.length - 1].name === stackFrame) {
          node = parentNodeChildren[parentNodeChildren.length - 1];
        }
      }
      if (node == null) {
        node = new Node();
        node.name = stackFrame;
        node.value = count;
        if (parentNodeChildren == null) {
          parentNode.children = [];
        }
        parentNode.children.push(node);
      } else {
        node.value += count;
      }
      parentNode = node;
    });
  }
}

// eslint-disable-next-line no-unused-vars
class FlameStack {
  constructor() {
    _defineProperty(this, "stackFrames", null);
    _defineProperty(this, "count", 1);
  }
  // 浅拷贝
  copy(other) {
    this.stackFrames = other.stackFrames;
    this.count = other.count;
    return this;
  }
}
class FlameInfo {
  constructor() {
    _defineProperty(this, "flameStacks", []);
    _defineProperty(this, "stackSumCounts", 0);
  }
}

/**
 * 获取flameStacks的选定区间的数据
 * @param flameStacks
 * @param sampleInterval
 * @param begin
 * @param end
 */

function convertFlameTextToFlameStacks(flameGraphText, reverseFrame) {
  const flameStackList = [];
  flameGraphText.split('\n').forEach(flameStackText => {
    if (flameStackText.length > 1) {
      const flameStack = new FlameStack();
      const endIndex = flameStackText.lastIndexOf(' ');
      const stack = flameStackText.slice(0, endIndex);
      const count = parseInt(flameStackText.slice(endIndex, flameStackText.len));
      let stackFrames = stack.split(';');
      if (reverseFrame) {
        stackFrames.reverse();
      }
      if (stack.charAt(stack.length - 1) === ';') {
        stackFrames.pop();
      }
      flameStack.stackFrames = stackFrames;
      flameStack.count = count;
      flameStackList.push(flameStack);
    }
  });
  return flameStackList;
}
function subFlameStacks(flameStacks, beginIndex, endIndex) {
  let index = 0;
  const flameStackList = [];
  let findBegin = false;
  // 从左到右遍历 找到 符合位置的

  for (let i = 0; i < flameStacks.length; i++) {
    const flameStack = flameStacks[i];
    if (!findBegin) {
      index = index + flameStack.count;
      if (index < beginIndex) {
        // continue step
      } else if (index >= beginIndex) {
        const temp = new FlameStack().copy(flameStack);
        temp.count = index - beginIndex;
        flameStackList.push(temp);
        findBegin = true;
      }
    } else {
      // for find end index
      index = index + flameStack.count;
      if (index < endIndex) {
        flameStackList.push(flameStack);
      } else if (index >= endIndex) {
        const temp = new FlameStack().copy(flameStack);
        temp.count = temp.count - (index - endIndex);
        flameStackList.push(temp);
        break;
      }
    }
  }
  return flameStackList;
}
function convertFlameStackListToNodeTree(flameStacks) {
  var root = new Node();
  flameStacks.forEach(flameStack => {
    root.addStack(flameStack.stackFrames, flameStack.count);
  });
  if (root.children != null && root.children.length === 1) {
    return root.children[0];
  }
  return root;
}
function convertFlameGraphTextToNodeTree(flameGraphText, reverseFrame) {
  var flameStacks = convertFlameTextToFlameStacks(flameGraphText, reverseFrame);
  return convertFlameStackListToNodeTree(flameStacks);
}