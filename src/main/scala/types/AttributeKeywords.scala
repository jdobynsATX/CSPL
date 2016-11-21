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

abstract sealed class StartKeyword extends IntTypedKeyword
object START extends StartKeyword

abstract sealed class EndKeyword extends IntTypedKeyword
object END extends EndKeyword

abstract sealed class DateKeyword extends IntTypedKeyword
object DATE extends DateKeyword