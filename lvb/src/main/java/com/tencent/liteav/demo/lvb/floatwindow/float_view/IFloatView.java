package com.tencent.liteav.demo.lvb.floatwindow.float_view;

/**
 * Description:悬浮窗抽象方法
 *
 * @author 杜乾-Dusan,Created on 2018/1/24 - 10:24.
 *         E-mail:duqian2010@gmail.com
 */
public interface IFloatView {
    public FloatViewParams getParams();
    public void setFloatViewListener(FloatViewListener listener);
}
