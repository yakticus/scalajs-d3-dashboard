package example

//import org.scalajs.d3._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

@JSExport
object ScalaJSExample {
  @JSExport
  def main(target: dom.HTMLElement): Unit = {
    val d3 = js.Dynamic.global.d3
    val nv = js.Dynamic.global.nv

    val element = d3.select(target).append("svg")
      .attr("id", "example-rect")
      .attr("width", 960)
      .attr("height", 500)
      .append("g")
      .attr("transform", s"translate(0,0)")

    val xhr = new dom.XMLHttpRequest()
    xhr.open("GET", "/target/scala-2.11/classes/timeseries1.json")
    xhr.onload = (e: dom.Event) => {
      if (xhr.status == 200) {
        val added = xhr.responseText.split("\n").mkString(",")
        val addedObj =
          s"""[{"key": "added",
         |"values":[$added]}]""".stripMargin

        nv.addGraph({
          val chart = nv.models.lineWithFocusChart()
            .x({(d: js.Dictionary[js.Number]) => d("timestep")}: js.Function)
            .y({(d: js.Dictionary[js.Number]) => d("value")}: js.Function)

          chart.xAxis
            .tickFormat(d3.format(",f"))

          chart.yAxis
            .tickFormat(d3.format(",.2f"))

          chart.y2Axis
            .tickFormat(d3.format(",.2f"))

          val parsed = JSON.parse(addedObj)
          element.datum(parsed).transition().duration(500).call(chart)

          nv.utils.windowResize(chart.update)

          chart
        })
      }
    }
    xhr.send()
  }


}
