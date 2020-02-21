/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dxzc.highlight;

/**
 *
 * @author 823984418@qq.com
 */
public class Main {

    public static void main(String[] args) {
        JavaScriptLineHighlight h = new JavaScriptLineHighlight();
        LineHighlight.OnHighlight s = (start, end, type) -> {
            System.out.println(start + ":" + end + "[" + type + "]");
            return true;
        };
        int d = h.defaultData();
        System.out.println(d);
        String l = "var a = 1;";
        d = h.scan(d, l.toCharArray(), 0, l.length());
        System.out.println(d);
        l = "/a/";
        h.highlight(d, l.toCharArray(), 0, l.length(), s);

    }

}
