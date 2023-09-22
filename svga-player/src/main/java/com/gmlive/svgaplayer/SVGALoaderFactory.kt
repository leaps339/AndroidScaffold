package com.gmlive.svgaplayer

/**

 * @Author svenj
 * @Date 2020/11/27
 * @Email svenjzm@gmail.com
 */
fun interface SVGALoaderFactory {

    fun newSvgaLoader(): SvgaLoader
}