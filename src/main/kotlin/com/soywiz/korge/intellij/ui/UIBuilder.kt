package com.soywiz.korge.intellij.ui

import com.intellij.openapi.util.IconLoader
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTabbedPane
import com.soywiz.korge.intellij.KorgeIcons
import org.intellij.lang.annotations.Language
import java.awt.*
import java.awt.image.BufferedImage
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.Border
import kotlin.collections.ArrayList

object UIBuilderSample {
	@JvmStatic
	fun main(args: Array<String>) {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
		val frame = JFrame()
		frame.minimumSize = Dimension(800, 600)
		frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
		frame.contentPane.layout = FillLayout()
		frame.contentPane.styled.createTileMapEditor()

		frame.pack()
		frame.setLocationRelativeTo(null)
		frame.isVisible = true
	}
}


fun Styled<out Container>.createLayerEditor(block: Styled<Container>.() -> Unit) {
	verticalStack {
		list(listOf("layer1", "layer2", "layer2")) {
			height = MUnit.Fill
		}
		toolbar {
			iconButton(toolbarIcon("add.png"))
			iconButton(toolbarIcon("up.png"))
			iconButton(toolbarIcon("down.png"))
			iconButton(toolbarIcon("delete.png"))
			iconButton(toolbarIcon("copy.png"))
			iconButton(toolbarIcon("show.png"))
			iconButton(toolbarIcon("locked_dark.png"))
		}
		block()
	}
}

fun Styled<out Container>.createTileMapEditor() {
	verticalStack {
		toolbar {
			button("Stamp")
			button("Dropper")
			button("Eraser")
			button("Fill")
			button("Rect")
			button("Poly")
			button("Point")
			button("Oval")
			iconButton(toolbarIcon("settings.png"))
			iconButton(toolbarIcon("zoomIn.png"))
			iconButton(toolbarIcon("zoomOut.png"))
			button("FlipX")
			button("FlipY")
			button("RotateL")
			button("RotateR")
		}
		horizontalStack {
			fill()
			verticalStack {
				width = 20.percentage
				tabs {
					height = 50.percentage
					tab("Properties") {
						verticalStack {
							list(listOf("prop1", "prop2", "prop3")) {
								height = MUnit.Fill
							}
							toolbar {
								iconButton(toolbarIcon("add.png"))
								iconButton(toolbarIcon("edit.png"))
								iconButton(toolbarIcon("delete.png"))
							}
						}
					}
				}
				tabs {
					height = 50.percentage
					tab("Tileset") {
						verticalStack {
							tabs {
								fill()
								tab("Untitled") {
									list(listOf("tileset")) {
										height = MUnit.Fill
									}
								}
							}
							toolbar {
								iconButton(toolbarIcon("add.png"))
								iconButton(toolbarIcon("openDisk.png"))
								iconButton(toolbarIcon("edit.png"))
								iconButton(toolbarIcon("delete.png"))
							}
						}
					}
				}
			}
			verticalStack {
				fill()
				tabs {
					fill()
					tab("Map") {
						createLayerEditor {
							fill()
						}
					}
				}
			}
			verticalStack {
				width = 20.percentage
				tabs {
					height = 50.percentage
					tab("Layers") {
						createLayerEditor {
							fill()
						}
					}
				}
				tabs {
					height = 50.percentage
					tab("Preview") {
					}
				}
			}
		}
		horizontalStack {
			height = 32.points
			label("Status Status 2")
			slider()
		}
	}
}

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class UIDslMarker

fun Styled<out Container>.button(text: String, block: @UIDslMarker Styled<JButton>.() -> Unit = {}) {
	component.add(JButton(text).also { block(it.styled) })
}

fun Styled<out Container>.label(text: String, block: @UIDslMarker Styled<JLabel>.() -> Unit = {}) {
	component.add(JLabel(text).also { block(it.styled) })
}

fun Styled<out Container>.slider(block: @UIDslMarker Styled<JSlider>.() -> Unit = {}) {
	component.add(JSlider().also { block(it.styled) })
}

fun <E : Any> Styled<out Container>.list(items: List<E> = listOf(), block: @UIDslMarker Styled<JList<E>>.() -> Unit = {}) {
	component.add(JList<E>(Vector(items.toMutableList())).also { block(it.styled) })
}

fun Styled<out Container>.iconButton(icon: Icon, tooltip: String? = null, block: @UIDslMarker Styled<JButton>.() -> Unit = {}) {
	//println("ICON: ${icon.iconWidth}x${icon.iconHeight}")
	component.add(
		JButton(icon)
			.also {
				it.iconTextGap = 0
				it.margin = Insets(0, 0, 0, 0)
				it.toolTipText = tooltip
				it.border = BorderFactory.createEmptyBorder()
			}
			.also {
				it.styled
					.also {
						it.preferred = MUnit2(32.points)
						it.min = MUnit2(32.points)
						it.max = MUnit2(32.points)
					}
					.block()
			}
	)
}

fun Styled<out Container>.toolbar(direction: Direction = Direction.HORIZONTAL, props: LinearLayout.Props = LinearLayout.Props(), block: @UIDslMarker Styled<JToolBar>.() -> Unit = {}): JToolBar {
	val container = JToolBar(if (direction.horizontal) JToolBar.HORIZONTAL else JToolBar.VERTICAL)
	container.styled.height = 32.points
	container.isFloatable = false
	container.layout = LinearLayout(if (direction.horizontal) Direction.HORIZONTAL else Direction.VERTICAL, props)
	component.add(container)
	block(container.styled)
	return container
}

fun Styled<out Container>.tabs(block: @UIDslMarker Styled<JBTabbedPane>.() -> Unit = {}): JBTabbedPane {
	val container = JBTabbedPane(JBTabbedPane.TOP)
	component.add(container)
	block(container.styled)
	return container
}

fun Styled<out JTabbedPane>.tab(title: String, block: @UIDslMarker Styled<JPanel>.() -> Unit = {}): JPanel {
	val container = JPanel()
	//container.title = title
	container.layout = FillLayout()
	component.addTab(title, container)
	block(container.styled)
	return container
}

fun Styled<out Container>.stack(direction: Direction, props: LinearLayout.Props,  block: @UIDslMarker Styled<Container>.() -> Unit = {}): Container {
	val container = Container()
	container.layout = LinearLayout(if (direction.horizontal) Direction.HORIZONTAL else Direction.VERTICAL, props)
	component.add(container)
	block(container.styled)
	return container
}

fun Styled<out Container>.verticalStack(props: LinearLayout.Props = LinearLayout.Props(), block: @UIDslMarker Styled<Container>.() -> Unit = {}) = stack(direction = Direction.VERTICAL, props = props, block = block)
fun Styled<out Container>.horizontalStack(props: LinearLayout.Props = LinearLayout.Props(), block: @UIDslMarker Styled<Container>.() -> Unit = {}) = stack(direction = Direction.HORIZONTAL, props = props, block = block)

fun icon(@Language("file-reference") path: String) = ImageIcon(ImageIO.read(UIBuilderSample::class.java.getResource(path)))
fun toolbarIcon(@Language("file-reference") path: String) = icon("/com/soywiz/korge/intellij/toolbar/$path")

val Container.root: Container get() = this.parent?.root ?: this

data class MUnit2(val width: MUnit, val height: MUnit) {
	constructor(size: MUnit) : this(size, size)
	fun dim(direction: Direction) = if (direction.horizontal) width else height
	fun with(direction: Direction, value: MUnit) = if (direction.horizontal) copy(width = value) else copy(height = value)
}

val FILL = MUnit.Fill
sealed class MUnit {
	abstract fun compute(total: Int): Int
	fun compute(total: Int, preferred: Int): Int {
		val result = compute(total)
		return if (result == Int.MIN_VALUE) preferred else result
	}

	object Auto : MUnit() {
		override fun compute(total: Int): Int = Int.MIN_VALUE
	}
	object Fill : MUnit() {
		override fun compute(total: Int): Int = total
	}
	class Ratio(val ratio: Double) : MUnit() {
		override fun compute(total: Int): Int = (total * ratio).toInt()
	}
	class Points(val points: Int) : MUnit() {
		override fun compute(total: Int): Int = points
	}
}

val Number.points get() = MUnit.Points(this.toInt())
val Number.ratio get() = MUnit.Ratio(this.toDouble())
val Number.percentage get() = (this.toDouble() / 100.0).ratio

private val styledWeakMap = WeakHashMap<Component, Styled<Component>>()

val <T : Component> T.styled: Styled<T> get() = styledWeakMap.getOrPut(this) { Styled(this) } as Styled<T>

class Styled<T : Component> constructor(val component: T) {
	var preferred: MUnit2 = MUnit2(MUnit.Auto, MUnit.Auto)
	var min: MUnit2 = MUnit2(0.points, 0.points)
	var max: MUnit2 = MUnit2(0.points, 0.points)

	var width: MUnit
		get() = preferred.width
		set(value) = run { preferred = preferred.copy( width = value) }

	var height: MUnit
		get() = preferred.height
		set(value) = run { preferred = preferred.copy( height = value) }

	fun fill() {
		width = FILL
		height = FILL
	}

	var minWidth: MUnit = 0.points
	var minHeight: MUnit = 0.points
	var maxWidth: MUnit = Int.MAX_VALUE.points
	var maxHeight: MUnit = Int.MAX_VALUE.points
	var padding: MUnit = 0.points
	var margin: MUnit = 0.points

	internal var temp: Int = 0
}

enum class Direction {
	VERTICAL, HORIZONTAL;
	val vertical get() = this == VERTICAL
	val horizontal get() = this == HORIZONTAL
}

class LinearLayout(
	val direction: Direction,
	val props: Props = Props()
) : LayoutAdapter() {
	data class Props(
		val growToFill: Boolean = false,
		val shrinkToFill: Boolean = false
	)

	fun Styled<Component>.dimension(direction: Direction) = if (direction == Direction.HORIZONTAL) width else height
	fun Styled<Component>.minDimension(direction: Direction) = if (direction == Direction.HORIZONTAL) minWidth else minHeight
	fun Styled<Component>.maxDimension(direction: Direction) = if (direction == Direction.HORIZONTAL) maxWidth else maxHeight

	//fun Styled<Component>.preferred(direction: Direction) = if (direction == Direction.HORIZONTAL) preferredDimensions.width else preferredDimensions.height
	fun Styled<Component>.preferred(direction: Direction) = if (direction == Direction.HORIZONTAL) component.preferredSize.width else component.preferredSize.height
	fun Styled<Component>.min(direction: Direction) = if (direction == Direction.HORIZONTAL) minDimensions.width else minDimensions.height
	fun Styled<Component>.max(direction: Direction) = if (direction == Direction.HORIZONTAL) maxDimensions.width else maxDimensions.height
	fun Component.size(direction: Direction) = if (direction == Direction.HORIZONTAL) width else height

	override fun layoutChildren(parent: Container, children: List<Styled<Component>>) {
		//println("${parent.size} : ${parent.root.size}")

		val containerSize = parent.size(direction)
		var childrenSize = 0
		var fillCount = 0
		for (doFill in listOf(false, true)) {
			val remaining = containerSize - childrenSize
			val fillSize = (if (fillCount > 0) remaining / fillCount else 0).coerceAtLeast(0)
			for (child in children) {
				val preferred = child.preferred(direction)
				val rdim = child.dimension(direction)
				val min = child.minDimension(direction).compute(containerSize, preferred)
				val dim = rdim.compute(containerSize, preferred)
				val max = child.maxDimension(direction).compute(containerSize, preferred)
				when (rdim) {
					is MUnit.Fill -> {
						if (doFill) {
							child.temp = fillSize.coerceIn(min, max)
							childrenSize += child.temp
						} else {
							fillCount++
						}
					}
					else -> {
						if (!doFill) {
							child.temp = dim.coerceIn(min, max)
							childrenSize += child.temp
						}
					}
				}
			}
		}

		if ((props.shrinkToFill && childrenSize > containerSize) || (props.growToFill && childrenSize < containerSize)) {
			val scale = containerSize.toDouble() / childrenSize.toDouble()
			for (child in children) {
				child.temp = (child.temp * scale).toInt()
			}
		}
		//println("childrenSize=$childrenSize, containerSize=$containerSize")

		//println("$containerSize, $remaining, $fillSize")

		var v = 0
		for (child in children) {
			when (direction) {
				Direction.VERTICAL -> child.component.setBounds(0, v, parent.width, child.temp)
				Direction.HORIZONTAL -> child.component.setBounds(v, 0, child.temp, parent.height)
			}
			v += child.temp
		}
		//println("layoutContainer: ${children.toList()}")
	}
}

class FillLayout : LayoutAdapter() {
	override fun layoutChildren(parent: Container, children: List<Styled<Component>>) {
		for (child in children) {
			child.component.setBounds(0, 0, parent.width, parent.height)
		}
	}
}

abstract class LayoutAdapter : LayoutManager2 {
	val minDimensions = Dimension(1, 1)
	val preferredDimensions = Dimension(128, 128)
	val maxDimensions = Dimension(2048, 2048)

	val children = ArrayList<Styled<Component>>()

	abstract fun layoutChildren(parent: Container, children: List<Styled<Component>>)

	override fun invalidateLayout(target: Container) {
	}

	override fun layoutContainer(parent: Container) {
		layoutChildren(parent, children)
	}

	override fun getLayoutAlignmentY(target: Container?): Float {
		return 0f
	}

	override fun getLayoutAlignmentX(target: Container?): Float {
		return 0f
	}

	override fun maximumLayoutSize(target: Container): Dimension = maxDimensions
	override fun preferredLayoutSize(parent: Container): Dimension = preferredDimensions
	override fun minimumLayoutSize(parent: Container): Dimension = minDimensions

	override fun addLayoutComponent(comp: Component, constraints: Any?) {
		//println("addLayoutComponent: $comp, $constraints")
		children.add(comp.styled)
	}

	override fun addLayoutComponent(name: String, comp: Component) {
		//println("addLayoutComponent: $name, $comp")
		children.add(comp.styled)
	}

	override fun removeLayoutComponent(comp: Component) {
		//println("removeLayoutComponent: $comp")
		children.remove(comp.styled)
	}
}
