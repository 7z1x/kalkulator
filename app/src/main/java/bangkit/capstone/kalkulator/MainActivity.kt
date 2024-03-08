package bangkit.capstone.kalkulator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var canAddOperation = false
    private var canAddDesimal = true
    private lateinit var workingTV: TextView
    private lateinit var resultTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        workingTV = findViewById(R.id.workingTV)
        resultTV = findViewById(R.id.resultTV)
    }

    fun numberAction(view: View)
    {
        if (view is Button)
        {
            if (view.text == ".")
            {
                if (canAddDesimal)
                    workingTV.append(view.text)
                canAddOperation = false
            }
            else
                workingTV.append(view.text)

            canAddOperation = true
        }
    }

    fun operationAction(view: View)
    {
        if (view is Button && canAddOperation)
        {
            workingTV.append(view.text)
            canAddOperation = false
            canAddDesimal = true
        }
    }

    fun allClearAction(view: View)
    {
        workingTV.text = ""
        resultTV.text = ""
    }

    fun backSpaceAction(view: View)
    {
        val length =  workingTV.length()
        if(length > 0)
            workingTV.text = workingTV.text.subSequence(0, length - 1)
    }

    fun equalsAction(view: View)
    {
        resultTV.text = calculateResult()
    }

    private fun calculateResult(): String
    {
        val digitsOperators = digitOperators()
        if (digitsOperators.isEmpty()) return ""
        
        val timesDivision = timesDivisionsCalculate(digitOperators())
        if (timesDivision.isEmpty()) return ""


        val result = addSubractCalculate(timesDivision)
        return result.toString()
    }

    private fun addSubractCalculate(passedList: MutableList<Any>): Float
    {
        var result = passedList[0] as Float

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex)
            {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }

        return result
    }

    private fun timesDivisionsCalculate(passedList: MutableList<Any>): MutableList<Any>
    {
        var list = passedList
        while (list.contains('x') || list.contains('/'))
        {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any>
    {
        val newList = mutableListOf<Any>()
        var restartindex = passedList.size

        for (i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex && i < restartindex)
            {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when(operator)
                {
                    'x' ->
                    {
                        newList.add(prevDigit * nextDigit)
                        restartindex = i + 1
                    }
                    '/' ->
                    {
                        newList.add(prevDigit / nextDigit)
                        restartindex = i + 1
                    }
                    else ->
                    {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            if (i > restartindex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitOperators(): MutableList<Any>
    {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in workingTV.text)
        {
            if (character.isDigit() || character == '.')
                currentDigit += character
            else
            {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }
        if(currentDigit != "")
            list.add(currentDigit.toFloat())

        return list
    }

}