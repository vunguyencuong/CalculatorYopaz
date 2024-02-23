package com.example.calculatoryopaz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.calculatoryopaz.databinding.ActivityMainBinding
import java.util.zip.DeflaterOutputStream

class MainActivity : AppCompatActivity() {

    private var canAddOperation = false
    private var canAddDecimal = false

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn0.setOnClickListener { getNumber(binding.btn0) }
        binding.btn1.setOnClickListener { getNumber(binding.btn1) }
        binding.btn2.setOnClickListener { getNumber(binding.btn2) }
        binding.btn3.setOnClickListener { getNumber(binding.btn3) }
        binding.btn4.setOnClickListener { getNumber(binding.btn4) }
        binding.btn5.setOnClickListener { getNumber(binding.btn5) }
        binding.btn6.setOnClickListener { getNumber(binding.btn6) }
        binding.btn7.setOnClickListener { getNumber(binding.btn7) }
        binding.btn8.setOnClickListener { getNumber(binding.btn8) }
        binding.btn9.setOnClickListener { getNumber(binding.btn9) }
        binding.btnDecimal.setOnClickListener { getNumber(binding.btnDecimal) }
        binding.btnPercent.setOnClickListener { getNumber(binding.btnPercent) }

        binding.btnAdd.setOnClickListener { getOperation(binding.btnAdd) }
        binding.btnSub.setOnClickListener { getOperation(binding.btnSub) }
        binding.btnMulti.setOnClickListener { getOperation(binding.btnMulti) }
        binding.btnDivision.setOnClickListener { getOperation(binding.btnDivision) }

        binding.btnC.setOnClickListener { clearAll() }
        binding.btnDelete.setOnClickListener { backSpace() }

        binding.btnSolve.setOnClickListener {
            binding.resultCal.text = calculateResults()
        }
    }

    private fun getNumber(view : View){
        if(view is Button){
            if(view.text == "."){
                if(canAddDecimal) binding.workingCal.append(view.text)
                canAddDecimal = false
            } else if(view.text == "%"){

                val result = calculateResults().toFloat() / 100
                binding.workingCal.text = result.toString()
                if(binding.resultCal.text != ""){
                    binding.resultCal.text = result.toString()
                }

            } else binding.workingCal.append(view.text)
            canAddOperation = true
        }
    }

    private fun getOperation(view : View){
        if(view is Button && canAddOperation){
            binding.workingCal.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    private fun clearAll(){
        binding.workingCal.text = ""
        binding.resultCal.text  = ""
    }

    private fun backSpace(){
        val length = binding.workingCal.length()
        if(length > 0) binding.workingCal.text = binding.workingCal.text.subSequence(0,length-1)
    }

    private fun calculateResults() : String {
        val digitOperators = getDigitOperators()
        if(digitOperators.isEmpty()) return ""

        val multiDivision = multiDivisionSolve(digitOperators)
        if(multiDivision.isEmpty()) return ""
        val result = addSubtractSolve(multiDivision)
        return result.toString()
    }

    private fun addSubtractSolve(list: MutableList<Any>) : Float{
        var result = list[0] as Float

        for(i in list.indices){
            if(list[i] is Char && i != list.lastIndex){
                val operator = list[i]
                val nextDigit = list[i+1] as Float
                if(operator == '+') result += nextDigit
                if(operator == '-') result -= nextDigit
            }
        }
        return result
    }

    private fun multiDivisionSolve(passedList : MutableList<Any>) : MutableList<Any> {
        var list = passedList
        while(list.contains('x') || list.contains('/')) list = calculatorMutliDiv(list)
        return list
    }

    private fun calculatorMutliDiv(passedList: MutableList<Any>) : MutableList<Any>{
        val newList = mutableListOf<Any>()
        var resetIndex = passedList.size
        for(i in passedList.indices){
            if(passedList[i] is Char && i != passedList.lastIndex && i < resetIndex){
                val operator = passedList[i]
                val prevDigit = passedList[i-1] as Float
                val nextDigit = passedList[i+1] as Float
                when(operator){
                    'x' ->
                    {
                        newList.add(prevDigit * nextDigit)
                        resetIndex = i + 1
                    }
                    '/' ->
                    {
                        newList.add(prevDigit / nextDigit)
                        resetIndex = i + 1
                    }
                    else ->
                    {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            if(i > resetIndex) newList.add(passedList[i])
        }
        return newList
    }

    private fun getDigitOperators() : MutableList<Any>{
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(c in binding.workingCal.text){
            if(c.isDigit() || c == '.') currentDigit += c
            else if(c == '%') list.add(currentDigit.toFloat()/100)
            else{
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(c)
            }
        }
        if(currentDigit != "") list.add(currentDigit.toFloat())
        return list
    }

}