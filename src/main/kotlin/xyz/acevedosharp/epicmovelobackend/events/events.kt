package xyz.acevedosharp.epicmovelobackend.events

import org.springframework.context.ApplicationEvent

class UpdateUsersCopyEvent(source: Any): ApplicationEvent(source)
class UpdateComponentsCopyEvent(source: Any): ApplicationEvent(source)
class DeleteBiciusuarioEvent(source: Any, val id: Int): ApplicationEvent(source)