#ifndef GODOT_LOADER_BRIDGE_H
#define GODOT_LOADER_BRIDGE_H


void* createInstance(void* instance,void* methodData);
void disposeClassHandle(void* ref);
void destroyInstance(void* instance, void* methodData, void* classData);

#endif //GODOT_LOADER_BRIDGE_H
