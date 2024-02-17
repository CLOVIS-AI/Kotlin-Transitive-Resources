package opensavvy.gradle.resources.examples.kmp.app

import kotlinx.browser.document

fun main() {
	document.getElementById("root")!!.innerHTML = """
		<div class="blue">Loaded Blue</div>
		<div class="yellow">Loaded Yellow</div>
		<div class="red">Loaded Red</div>
	""".trimIndent()
}
