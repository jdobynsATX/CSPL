package cs345.bdsl

/**
  * Created by Sean on 11/9/16.
  */

abstract sealed class ObjectKeyword

abstract sealed class EmployeeKeyword extends ObjectKeyword
object EMPLOYEE extends EmployeeKeyword

abstract sealed class ClientKeyword extends ObjectKeyword
object CLIENT extends ClientKeyword

abstract sealed class EventKeyword extends ObjectKeyword
object EVENT extends EventKeyword