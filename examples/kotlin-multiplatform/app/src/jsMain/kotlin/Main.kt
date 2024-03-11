package opensavvy.gradle.resources.examples.kmp.app

import kotlinx.browser.document

fun main() {
	document.getElementById("root")!!.innerHTML = """
		<div class="blue">This should be blue (from core/commonMain)</div>
		<div class="yellow">This should be yellow (from core/webMain)</div>
		<div class="red">This should be red (from core/jsMain)</div>
		<div class="green">This should NOT be green (from core/wasmJsMain)</div>
	""".trimIndent()
}
