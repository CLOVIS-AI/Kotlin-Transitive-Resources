package opensavvy.gradle.resources.examples.kmp.app

import kotlinx.browser.document

fun main() {
	document.getElementById("root")!!.innerHTML = "Loaded!"
}
