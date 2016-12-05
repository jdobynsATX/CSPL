package cs345.bdsl
/**
  * Created by Sean on 11/9/16.
  */
abstract class AttributeKeyword
abstract sealed class StringTypedKeyword extends AttributeKeyword
abstract sealed class IntTypedKeyword extends AttributeKeyword
abstract sealed class DoubleTypedKeyword extends AttributeKeyword

abstract sealed class IdKeyword extends IntTypedKeyword
object ID extends IdKeyword

abstract sealed class NameKeyword extends StringTypedKeyword
object NAME extends NameKeyword

abstract sealed class RankKeyword extends IntTypedKeyword
object RANK extends RankKeyword

abstract sealed class PayKeyword extends DoubleTypedKeyword
object PAY extends PayKeyword

abstract sealed class StartKeyword extends StringTypedKeyword
object START extends StartKeyword

abstract sealed class EndKeyword extends StringTypedKeyword
object END extends EndKeyword

abstract sealed class DurationKeyword extends IntTypedKeyword
object DURATION extends DurationKeyword

abstract sealed class DateKeyword extends StringTypedKeyword
object DATE extends DateKeyword

abstract sealed class Client_IdKeyword extends IntTypedKeyword
object CLIENT_ID extends Client_IdKeyword

abstract sealed class Emp_IdKeyword extends IntTypedKeyword
object EMP_ID extends Emp_IdKeyword

abstract sealed class AmountKeyword extends DoubleTypedKeyword
object AMOUNT extends AmountKeyword

abstract sealed class BalanceKeyword extends DoubleTypedKeyword
object BALANCE extends BalanceKeyword

abstract sealed class Total_CostKeyword extends DoubleTypedKeyword
object TOTAL_COST extends Total_CostKeyword

abstract sealed class Total_EarningKeyword extends DoubleTypedKeyword
object TOTAL_EARNING extends Total_EarningKeyword

abstract sealed class QuantityKeyword extends IntTypedKeyword
object QUANTITY extends QuantityKeyword

abstract sealed class Inv_IdKeyword extends IntTypedKeyword
object INV_ID extends Inv_IdKeyword

abstract sealed class ReceivedKeyword extends StringTypedKeyword
object RECEIVED extends ReceivedKeyword

abstract sealed class Purchase_DateKeyword extends StringTypedKeyword
object PURCHASE_DATE extends Purchase_DateKeyword