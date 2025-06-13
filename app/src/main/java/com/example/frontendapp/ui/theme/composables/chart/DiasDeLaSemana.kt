package com.example.frontendapp.ui.theme.composables.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.frontendapp.data.model.Reserva.ReservaPorDiaDTO
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import kotlinx.coroutines.runBlocking

@Composable
fun LineChart(
    modelProducer: CartesianChartModelProducer,
    xLabels: List<String>,
    modifier: Modifier = Modifier,
) {

    val lineColor = Color(0xffa485e0)
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                        areaFill =
                        LineCartesianLayer.AreaFill.single(
                            fill(
                                ShaderProvider.verticalGradient(
                                    arrayOf(lineColor.copy(alpha = 0.4f), Color.Transparent)
                                )
                            )
                        ),
                    )
                ),
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = { _, x, _ ->
                    xLabels.getOrNull(x.toInt()) ?: ""
                }
            ),

        ),

        modelProducer = modelProducer,
        modifier = modifier,
    )
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    val sampleData = listOf(
        ReservaPorDiaDTO("Mon", 5),
        ReservaPorDiaDTO("Tue", 8),
        ReservaPorDiaDTO("Wed", 6),
        ReservaPorDiaDTO("Thu", 3),
        ReservaPorDiaDTO("Fri", 7),
        ReservaPorDiaDTO("Sat", 9),
        ReservaPorDiaDTO("Sun", 4)
    )

    val modelProducer = remember { CartesianChartModelProducer() }

    val xLabels = sampleData.map { it.dia }

    runBlocking {
        modelProducer.runTransaction {
            lineSeries {
                series(sampleData.map { it.cantidad.toFloat() })
            }
        }
    }

    LineChart(
        modelProducer = modelProducer,
        xLabels = xLabels
    )
}
