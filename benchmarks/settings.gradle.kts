includeBuild("../") {
    dependencySubstitution {
        substitute(module("com.utopia-rise:godot-library")).with(project(":godot-library"))
    }
}