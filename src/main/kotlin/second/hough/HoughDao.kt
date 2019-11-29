package second.hough

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object HoughDao {
    private val houghLineFilter = HoughLineFilter()
    fun houghEdit(flag:Int,fileName: String): BufferedImage {
        when(flag){
            1->houghLineFilter.setAttribute(500,0.7f,6)
            2->houghLineFilter.setAttribute(500,0.5f,30)
            3->houghLineFilter.setAttribute(1800,0.3f,50)
        }
        val img = ImageIO.read(File(fileName))
        //val resulting = lineFilter.lineFilter(img)

        //val resulting = houghLine.hough(img)
        return houghLineFilter.filter(img)
    }

    fun houghEditByImage(flag:Int,img:BufferedImage): BufferedImage {
        when(flag){
            1->houghLineFilter.setAttribute(500,0.7f,6)
            2->houghLineFilter.setAttribute(500,0.5f,30)
            3->houghLineFilter.setAttribute(1800,0.3f,50)
        }

        //val resulting = lineFilter.lineFilter(img)

        //val resulting = houghLine.hough(img)
        return houghLineFilter.filter(img)
    }
}