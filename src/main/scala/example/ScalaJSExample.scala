package example

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

@JSExport
object ScalaJSExample {
  val urlPrefix = "/target/scala-2.11/classes/"
  val chartDefUrl = urlPrefix + "chart-definitions.json"
  var chartData = List.empty[String]

  @JSExport
  def main(target: dom.HTMLElement): Unit = {
    Ajax.get(chartDefUrl).onSuccess {
      case xhr =>
        val chartDefs = JSON.parse(xhr.responseText)
        val array = chartDefs.asInstanceOf[js.Array[js.Dictionary[js.String]]]
        val chartDiv = document.createElement("div")
        target.appendChild(chartDiv)

        array.foreach {
          e =>
            val chartTitle = e.apply("name").toString
            val title = document.createElement("h3")
            title.textContent = chartTitle
            chartDiv.appendChild(title)

            val dataSources = e.apply("series").asInstanceOf[js.Array[js.String]]
            var urls = List.empty[String]
            dataSources.forEach((s: js.String) => urls = (urlPrefix + s.toString) :: urls)
            println(urls)
            getChartData(urls.toList, chartDiv)
        }
    }
  }

  def getChartData(timeseriesUrls: List[String], target: dom.HTMLElement): Unit = {
    val timeseriesFuture = Future.sequence(timeseriesUrls.map(url => Ajax.get(url)))

    timeseriesFuture.onSuccess {
      case responses =>
        val json = responses.zipWithIndex.map {
          case (xhr, index) =>
            val valueArray = xhr.responseText.split("\n").mkString(",")
            s"""{"key": "series$index","values":[$valueArray]}"""
        }.mkString("[", ",", "]")
        renderChart(json, target)
    }
  }

  def renderChart(json: String, target: dom.HTMLElement): Unit = {
    val nv = js.Dynamic.global.nv
    val d3 = js.Dynamic.global.d3

    val element = d3.select(target).append("svg")
      .attr("id", "example-rect")
      .attr("width", 960)
      .attr("height", 500)
      .append("g")
      .attr("transform", s"translate(0,0)")

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

      val parsed = JSON.parse(json)
      element.datum(parsed).transition().duration(500).call(chart)

      nv.utils.windowResize(chart.update)

      chart
    })
  }

}
