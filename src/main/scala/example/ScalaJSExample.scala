package example

//import org.scalajs.d3._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.{JSON, Date}
import scala.scalajs.js.annotation.JSExport

@JSExport
object ScalaJSExample {
  val as = """{"timestep":1, "value":0}
             |{"timestep":2, "value":0}
             |{"timestep":3, "value":0}
             |{"timestep":4, "value":3}
             |{"timestep":5, "value":1}
             |{"timestep":6, "value":5}
             |{"timestep":7, "value":3}
             |{"timestep":8, "value":2}
             |{"timestep":9, "value":17}
             |{"timestep":10, "value":1}
             |{"timestep":11, "value":5}
             |{"timestep":12, "value":10}
             |{"timestep":13, "value":6}
             |{"timestep":14, "value":4}
             |{"timestep":15, "value":6}
             |{"timestep":16, "value":1}
             |{"timestep":17, "value":13}
             |{"timestep":18, "value":7}
             |{"timestep":19, "value":0}
             |{"timestep":20, "value":7}
             |{"timestep":21, "value":2}
             |{"timestep":22, "value":1}
             |{"timestep":23, "value":0}
             |{"timestep":24, "value":0}
             |{"timestep":25, "value":0}
             |{"timestep":26, "value":3}
             |{"timestep":27, "value":8}
             |{"timestep":28, "value":13}
             |{"timestep":29, "value":5}
             |{"timestep":30, "value":4}
             |{"timestep":31, "value":28}
             |{"timestep":32, "value":0}
             |{"timestep":33, "value":0}
             |{"timestep":34, "value":0}
             |{"timestep":35, "value":1}
             |{"timestep":36, "value":0}
             |{"timestep":37, "value":10}
             |{"timestep":38, "value":1}
             |{"timestep":39, "value":15}
             |{"timestep":40, "value":28}
             |{"timestep":41, "value":0}
             |{"timestep":42, "value":0}
             |{"timestep":43, "value":0}
             |{"timestep":44, "value":4}
             |{"timestep":45, "value":3}
             |""".stripMargin

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
          val chart = nv.models.stackedAreaChart()
            .margin(js.Dynamic.literal("right" -> 100))
            .x({(d: js.Dictionary[js.Number]) => d("timestep")}: js.Function)   //We can modify the data accessor functions...
            .y({(d: js.Dictionary[js.Number]) => d("value")}: js.Function)   //...in case your data is formatted differently.
            .useInteractiveGuideline(true)    //Tooltips which show all data points. Very nice!
            .rightAlignYAxis(true)      //Let's move the y-axis to the right side.
            .transitionDuration(500)
            .showControls(true)       //Allow user to choose 'Stacked', 'Stream', 'Expanded' mode.
            .clipEdge(true)

          //Format x-axis labels with custom function.
          chart.xAxis.tickFormat({(d: Double) => d3.time.format("%x")(new Date(d))}: js.ThisFunction)

          chart.yAxis.tickFormat(d3.format(",.2f"))

          val parsed = JSON.parse(addedObj)
          element.datum(parsed).call(chart)

          nv.utils.windowResize(chart.update)

          chart
        })
      }
    }
    xhr.send()



  }
}
