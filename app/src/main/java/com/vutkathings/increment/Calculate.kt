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

    private val utf8 :String = "UTF-8"

    companion object {
        fun builder (uuid:String , operandOne:Int , operandTwo:Int , operator: Operator , result:Int) : Calculate {
            val message = Calculate()
            message.uuid = uuid
            message.operandOne = operandOne
            message.operandTwo = operandTwo
            message.operator = operator
            message.result = result

            return message
        }
    }


    private var uuid:String? = null

    @Bindable
    private var operandOne:Int ? = null
    set(value) {
        field = value
        notifyPropertyChanged(BR.operandOne)
    }


    @Bindable
    private var operandTwo : Int ? = null
    set(value) {
        field = value
        notifyPropertyChanged(BR.operandTwo)
    }


    @Bindable
    private var operator : Operator ? = null
    set(value) {
        field = value
        notifyPropertyChanged(BR.operator)
    }

    @Bindable
    private var result : Int ? = null
    set(value) {
        field = value
        notifyPropertyChanged(BR.result)
    }


    fun toMessage() : Message = Message(Gson().toJson(this).toByteArray(Charset.forName(utf8)))

    fun fromMessage(message: Message) : Calculate{
        val nearbyDeviceMessageString = String(message.content).trim { it <= ' ' }

        return Gson().fromJson(String(nearbyDeviceMessageString.toByteArray(Charset.forName(utf8))),
                Calculate::class.java)
    }

    override fun toString(): String {
        return "ID : $uuid \nOperator: $operator \nOperand One: $operandOne \nOperand Two: $operandTwo"
    }
}