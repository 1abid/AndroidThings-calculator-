package com.vutkathings.calculation

import android.databinding.Bindable
import com.google.android.gms.nearby.messages.Message
import com.google.gson.Gson
import java.io.Serializable
import java.nio.charset.Charset


/**
 * Created by Abid Hasan on 14/12/17.
 */

class Calculation : Serializable{


    companion object {

        private val utf8: String = "UTF-8"

        fun builder(uuid: String, operandOne: String, operandTwo: String, operator: Operator): Calculation {
            val message = Calculation()
            message.uuid = uuid
            message.operandOne = operandOne
            message.operandTwo = operandTwo

            when (operator) {
                Operator.OPERATOR_PLUS -> message.operator = "Plus"
                Operator.OPERATOR_MINUS -> message.operator = "Minus"
                Operator.OPERATOR_MULTIPLY -> message.operator = "Multiply"
                else -> message.operator = "Divide"
            }

            return message
        }


        fun fromMessage(message: Message): Calculation {
            val nearbyDeviceMessageString = String(message.content).trim { it <= ' ' }

            return Gson().fromJson(String(nearbyDeviceMessageString.toByteArray(Charset.forName(utf8))),
                    Calculation::class.java)
        }
    }


    private var uuid: String? = null


    var operandOne: String? = null


    var operandTwo: String? = null


    var operator: String? = null


    var result: String? = null


    fun toMessage(): Message = Message(Gson().toJson(this).toByteArray(Charset.forName(utf8)))


    override fun toString(): String {
        return "ID : $uuid \nOperator: $operator \nOperand One: $operandOne \nOperand Two: $operandTwo"
    }


}