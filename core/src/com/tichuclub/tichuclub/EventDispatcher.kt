package com.tichuclub.tichuclub

class EventDispatcher(private var listeners: ArrayList<TichuEventListener>) {

    fun addListener(listener: TichuEventListener) {
        listeners.add(listener)
    }

    fun dispatch(event: TichuEvent) {
        for(listener in listeners) {
            if(listener.event.name == event.name) {
                listener.respond()
            }
        }
    }

}