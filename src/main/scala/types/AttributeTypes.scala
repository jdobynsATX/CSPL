package cs345.bdsl
/**
  * Created by Sean on 11/9/16.
  */
abstract class AttributeKeyword
abstract sealed class StringTyped extends AttributeKeyword
abstract sealed class IntTyped extends AttributeKeyword

abstract sealed class IdKeyword extends IntTyped
object ID extends IdKeyword

abstract sealed class NameKeyword extends StringTyped
object NAME extends NameKeyword

abstract sealed class RankKeyword extends IntTyped
object RANK extends RankKeyword