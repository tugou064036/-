package com.example.miaomiao.data

import androidx.room.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
@TypeConverters(Converters::class)
data class Transaction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String, // 关联用户ID
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val description: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val emoji: String = ""
)

enum class TransactionType {
    INCOME, EXPENSE
}

enum class TransactionCategory(val displayName: String, val emoji: String) {
    // 支出类别
    FOOD("餐饮", "🍽️"),
    TRANSPORT("交通", "🚗"),
    SHOPPING("购物", "🛍️"),
    ENTERTAINMENT("娱乐", "🎮"),
    HEALTHCARE("医疗", "🏥"),
    EDUCATION("教育", "📚"),
    UTILITIES("生活缴费", "💡"),
    HOUSING("住房", "🏠"),
    OTHER_EXPENSE("其他支出", "💸"),
    
    // 收入类别
    SALARY("工资", "💰"),
    BONUS("奖金", "🎁"),
    INVESTMENT("投资收益", "📈"),
    GIFT("礼金", "🎁"),
    FREELANCE("兼职收入", "💼"),
    OTHER_INCOME("其他收入", "💵")
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val username: String,
    @ColumnInfo(name = "phone") val phone: String,
    val password: String, // 新增密码字段
    val avatar: String = "🐱", // 默认猫咪头像
    val totalDays: Int = 0,
    val totalTransactions: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

class Converters {
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
    
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let {
            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }
}