//
// Created by Administrator on 2023/2/4.
//

#ifndef BLOCKCANARYX_TOUCHEVENTTRACER_H
#define BLOCKCANARYX_TOUCHEVENTTRACER_H

#endif //BLOCKCANARYX_TOUCHEVENTTRACER_H

class TouchEventTracer {
public:
    static void touchRecv(int fd);
    static void touchSendFinish(int fd);
    static void start(int threshold);
};