#ifndef GODOT_LOADER_BRIDGE_H
#define GODOT_LOADER_BRIDGE_H
#include "gdnative_api_struct.gen.h"

void* createInstance(void* instance,void* methodData);
void disposeClassHandle(void* ref);
void destroyInstance(void* instance, void* methodData, void* classData);


void disposeFuncHandle(void* ref);
godot_variant invokeMethod(void* instance, void* methodData, void* classData, int numArgs, godot_variant** args);

#endif //GODOT_LOADER_BRIDGE_H
