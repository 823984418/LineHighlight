/*
 * Copyright (c) 2020, 823984418@qq.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.dxzc.highlight;

/**
 * 行高亮器. 务必保证传入内容中没有换行符, 得出的标记结果可能会把一个词素分为部分标记
 *
 * @param <T> 储存前文的类,应该覆盖{@link Object#equals}方法来降低分析工作量
 * @author 823984418@qq.com
 */
public interface LineHighlight<T> {

    /**
     * 结果的接收者. 保证顺序返回,因此,如果超出屏幕而不需要则可以返回{@code false}以停止分析行为
     */
    public static interface OnHighlight {

        /**
         * 接受一个标记.
         *
         * @param start 起点
         * @param end 终点
         * @param type 类型,由高亮器规定
         * @return 是否需要继续
         */
        public boolean onHighlight(int start, int end, int type);

    }

    /**
     * 得到默认的前文以作为第一行的前文.
     *
     * @return 前文储存
     */
    public T defaultData();

    /**
     * 仅扫描一行的基本框架. 以此方法来刷新给后一行的前文
     *
     * @param data 给此行的前文
     * @param text 缓冲
     * @param start 行内容起点
     * @param end 行内容重点
     * @return
     */
    public T scan(T data, char[] text, int start, int end);

    /**
     * 扫描一行并给出高亮.
     *
     * @param data 给此行的前文
     * @param text 缓冲
     * @param start 行内容起点
     * @param end 行内容重点
     * @param callback 返回接收者
     */
    public void highlight(T data, char[] text, int start, int end, OnHighlight callback);

}
