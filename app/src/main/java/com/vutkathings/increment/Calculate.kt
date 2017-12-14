package com.vutkathings.increment

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.android.gms.nearby.messages.Message
import com.google.gson.Gson
import java.io.Serializable
import java.nio.charset.Charset


/**
 * Created by Abid Hasan on 9/12/17.
 */

class Calculate : BaseObservable() , Serializable {



    companion object {

        private val utf8 :String = "UTF-8"

        fun builder (uuid:String , operandOne:String , operandTwo:String , operator: Operator ) : Calculate {
            val message = Calculate()
            message.uuid = uuid
            message.operandOne = operandOne
            message.operandTwo = operandTwo

            when(operator){
                Operator.OPERATOR_PLUS -> message.operator = "Plus"
                Operator.OPERATOR_MINUS -> message.operator = "Minus"
                Operator.OPERATOR_MULTIPLY -> message.operator = "Multiply"
                else -> message.operator = "Divide"
            }

            return message
        }


        fun fromMessage(message: Message) : Calculate{
            val nearbyDeviceMessageString = String(message.content).trim { it <= ' ' }

            return Gson().fromJson(String(nearbyDeviceMessageString.toByteArray(Charset.forName(utf8))),
                    Calculate::class.java)
        }
    }


    private var uuid:String? = null

    @Bindable
    var operandOne:String ? = null
    set(value) {
        field = value
        notifyPropertyChanged(BR.operandOne)
    }


    @Bindable
    var operandTwo : String ? = null
    set(value) {

        field = value
        notifyPropertyChanged(BR.operandTwo)
    }


    @Bindable
    var operator : String ? = null
    set(value) {
        field = value
        notifyPropertyChanged(BR.operator)
    }

    @Bindable
    var result : String ? = null
    set(value) {
        field = value
        notifyPropertyChanged(BR.result)
    }



    fun toMessage() : Message = Message(Gson().toJson(this).toByteArray(Charset.forName(utf8)))



    override fun toString(): String {
        return "ID : $uuid \nOperator: $operator \nOperand One: $operandOne \nOperand Two: $operandTwo"
    }
}