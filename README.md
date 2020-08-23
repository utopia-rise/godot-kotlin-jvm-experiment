# godot-kotlin-jvm

**NOTE:** This is just a PoC (Proof of concept) repository! The work here is finished as we have "proven our concept" ;-). The "real" implementation is in [this](https://github.com/utopia-rise/godot-jvm) repository.

This repository contains different approaches to achieve running kotlin on the jvm inside godot:

| Branch | Description |
| ------ | ----------- |
| cpp_poc | Original first very rudimentary PoC which uses cpp as bridge to start an embedded jvm using jni |
| ranie/rust-jni | PoC which uses rust instead of cpp as glue layer and uses jni to interact with the jvm. Discontinued because of development overhead with jni and godot inside rust |
| ranie/kn-jni | PoC which uses K/N instead of cpp as glue layer and uses jni to interact with jvm. Discontinued because of sever perf impact from crossing boundaries many times (godot(cpp) -> K/N -> JNI(cpp) -> JVM -> JNI(cpp) -> K/N -> godot(cpp) for a simple call to a function |
| ranie/cpp-jvm | Same approach as the original PoC but setup in a better way. Cpp is used as glue code to interact with the jvm using jni and to embedd a jvm. Also the final poc which is used for benchmarks against GDScript and our K/N binding. This approach will be used in `godot-jvm` but not as a GDNative binding as done in this poc, rather as a module like the C# bindings to furthere reduce layering and inproove perf and integration |

The most relevant branch (and the only one containing some instruction to test for yourself) is `ranie/cpp-jvm`. The `godot-jvm` project is based on the insigths gained in that branch.