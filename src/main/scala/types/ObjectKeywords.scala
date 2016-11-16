package cs345.bdsl

/**
  * Created by Sean on 11/9/16.
  */

abstract sealed class ObjectKeyword

abstract sealed class EmployeeKeyword extends ObjectKeyword
object EMPLOYEE extends EmployeeKeyword

abstract sealed class ItemKeyword extends ObjectKeyword
object ITEM extends ItemKeyword