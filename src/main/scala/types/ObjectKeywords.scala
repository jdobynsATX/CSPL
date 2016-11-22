package cs345.bdsl

/**
  * Created by Sean on 11/9/16.
  */

abstract sealed class ObjectKeyword

abstract sealed class EmployeeKeyword extends ObjectKeyword
object EMPLOYEE extends EmployeeKeyword

abstract sealed class ClientKeyword extends ObjectKeyword
object CLIENT extends ClientKeyword

abstract sealed class MeetingKeyword extends ObjectKeyword
object MEETING extends MeetingKeyword

abstract sealed class ProjectKeyword extends ObjectKeyword
object PROJECT extends ProjectKeyword
