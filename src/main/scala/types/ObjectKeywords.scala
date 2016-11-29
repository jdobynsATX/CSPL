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

abstract sealed class EventKeyword extends ObjectKeyword
object EVENT extends EventKeyword

abstract sealed class PaymentKeyword extends ObjectKeyword
object PAYMENT extends PaymentKeyword

abstract sealed class PurchaseKeyword extends ObjectKeyword
object PURCHASE extends PurchaseKeyword

abstract sealed class ShipmentKeyword extends ObjectKeyword
object SHIPMENT extends ShipmentKeyword

abstract sealed class InventoryKeyword extends ObjectKeyword
object INVENTORY extends InventoryKeyword

abstract sealed class MeetingAssignKeyword extends ObjectKeyword
object MEETING_ASSIGNMENTS extends MeetingAssignKeyword

abstract sealed class ProjectAssignKeyword extends ObjectKeyword
object PROJECT_ASSIGNMENTS extends ProjectAssignKeyword