package com.techtool.giftsuggester

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

data class GiftSuggestion(
    val name: String,
    val price: String,
    val description: String,
    val occasion: String,
    val category: String
)

class GiftSuggesterViewModel : ViewModel() {
    private val _suggestions = mutableStateOf<List<GiftSuggestion>>(emptyList())
    val suggestions: State<List<GiftSuggestion>> = _suggestions

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _hasResults = mutableStateOf(false)
    val hasResults: State<Boolean> = _hasResults

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    private val _uploadedImage = mutableStateOf<Bitmap?>(null)
    val uploadedImage: State<Bitmap?> = _uploadedImage

    private val _occasion = mutableStateOf("")
    val occasion: State<String> = _occasion

    private val _recipientInfo = mutableStateOf("")
    val recipientInfo: State<String> = _recipientInfo

    private val _budget = mutableStateOf("")
    val budget: State<String> = _budget

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = "AIzaSyAR_TU2bYG1n20eXflXgtAWcd7YXJvD9IY"
    )

    fun setUploadedImage(bitmap: Bitmap?) {
        _uploadedImage.value = bitmap
    }

    fun setOccasion(occasion: String) {
        _occasion.value = occasion
    }

    fun setRecipientInfo(info: String) {
        _recipientInfo.value = info
    }

    fun setBudget(budget: String) {
        _budget.value = budget
    }

    fun suggestGiftsFromText() {
        _isLoading.value = true
        _hasResults.value = false
        _errorMessage.value = ""
        _suggestions.value = emptyList()

        viewModelScope.launch {
            try {
                val prompt = """
                    I need gift suggestions based on the following information:

                    Occasion: ${_occasion.value}
                    Recipient Information: ${_recipientInfo.value}
                    Budget: ${_budget.value}

                    Please provide exactly 6 unique and thoughtful gift suggestions in this EXACT format:

                    GIFT 1:
                    NAME: [Gift name]
                    PRICE: [Estimated price range]
                    DESCRIPTION: [2-3 sentence description of the gift]
                    OCCASION: [Suitable occasions]
                    CATEGORY: [Gift category like Electronics, Fashion, Books, etc.]

                    GIFT 2:
                    NAME: [Gift name]
                    PRICE: [Estimated price range]
                    DESCRIPTION: [2-3 sentence description of the gift]
                    OCCASION: [Suitable occasions]
                    CATEGORY: [Gift category]

                    [Continue for all 6 gifts]

                    Make sure the suggestions are:
                    - Unique and creative
                    - Within the specified budget
                    - Appropriate for the occasion
                    - Personalized based on recipient information
                    - Practical and thoughtful
                """.trimIndent()

                val response = model.generateContent(prompt)
                val responseText = response.text

                if (responseText != null && responseText.isNotEmpty()) {
                    _suggestions.value = parseGiftSuggestions(responseText)
                    _hasResults.value = _suggestions.value.isNotEmpty()

                    if (_suggestions.value.isEmpty()) {
                        _errorMessage.value = "Unable to generate suggestions. Please try again."
                    }
                } else {
                    _errorMessage.value = "No response received. Please try again."
                }

            } catch (e: Exception) {
                _errorMessage.value = when {
                    e.message?.contains("API") == true -> "API Error: Please check your internet connection"
                    e.message?.contains("network") == true -> "Network Error: Please check your internet connection"
                    else -> "Error: ${e.message ?: "Unknown error occurred"}"
                }
                _hasResults.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun suggestGiftsFromImage(bitmap: Bitmap) {
        _isLoading.value = true
        _hasResults.value = false
        _errorMessage.value = ""
        _suggestions.value = emptyList()

        viewModelScope.launch {
            try {
                val prompt = """
                    Analyze this image and suggest 6 unique and thoughtful gifts based on what you see.
                    Consider the person's interests, style, hobbies, or context shown in the image.

                    Additional context:
                    Occasion: ${_occasion.value.ifEmpty { "Any occasion" }}
                    Budget: ${_budget.value.ifEmpty { "Flexible" }}
                    Additional info: ${_recipientInfo.value.ifEmpty { "None" }}

                    Provide exactly 6 gift suggestions in this EXACT format:

                    GIFT 1:
                    NAME: [Gift name]
                    PRICE: [Estimated price range]
                    DESCRIPTION: [2-3 sentence description explaining why this gift suits the person based on the image]
                    OCCASION: [Suitable occasions]
                    CATEGORY: [Gift category like Electronics, Fashion, Books, Sports, etc.]

                    GIFT 2:
                    NAME: [Gift name]
                    PRICE: [Estimated price range]
                    DESCRIPTION: [2-3 sentence description]
                    OCCASION: [Suitable occasions]
                    CATEGORY: [Gift category]

                    [Continue for all 6 gifts]

                    Make the suggestions creative, personalized, and based on visual cues from the image.
                """.trimIndent()

                val response = model.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )

                val responseText = response.text
                if (responseText != null && responseText.isNotEmpty()) {
                    _suggestions.value = parseGiftSuggestions(responseText)
                    _hasResults.value = _suggestions.value.isNotEmpty()

                    if (_suggestions.value.isEmpty()) {
                        _errorMessage.value = "Unable to generate suggestions from image. Please try again."
                    }
                } else {
                    _errorMessage.value = "No response received. Please try again."
                }

            } catch (e: Exception) {
                _errorMessage.value = when {
                    e.message?.contains("API") == true -> "API Error: Please check your internet connection"
                    e.message?.contains("network") == true -> "Network Error: Please check your internet connection"
                    else -> "Error: ${e.message ?: "Unknown error occurred"}"
                }
                _hasResults.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseGiftSuggestions(response: String): List<GiftSuggestion> {
        val suggestions = mutableListOf<GiftSuggestion>()

        try {
            val giftBlocks = response.split("GIFT ").filter { it.isNotBlank() }

            for (block in giftBlocks) {
                try {
                    val name = extractField(block, "NAME:")
                    val price = extractField(block, "PRICE:")
                    val description = extractField(block, "DESCRIPTION:")
                    val occasion = extractField(block, "OCCASION:")
                    val category = extractField(block, "CATEGORY:")

                    if (name.isNotEmpty() && description.isNotEmpty()) {
                        suggestions.add(
                            GiftSuggestion(
                                name = name,
                                price = price.ifEmpty { "Price varies" },
                                description = description,
                                occasion = occasion.ifEmpty { "Any occasion" },
                                category = category.ifEmpty { "General" }
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Skip malformed gift blocks
                    continue
                }
            }
        } catch (e: Exception) {
            // Return empty list if parsing fails
        }

        return suggestions
    }

    private fun extractField(text: String, field: String): String {
        return try {
            val fieldIndex = text.indexOf(field)
            if (fieldIndex == -1) return ""

            val startIndex = fieldIndex + field.length
            val endIndex = text.indexOf("\n", startIndex).takeIf { it != -1 }
                ?: text.indexOf("NAME:", startIndex).takeIf { it != -1 }
                ?: text.indexOf("PRICE:", startIndex).takeIf { it != -1 }
                ?: text.indexOf("DESCRIPTION:", startIndex).takeIf { it != -1 }
                ?: text.indexOf("OCCASION:", startIndex).takeIf { it != -1 }
                ?: text.indexOf("CATEGORY:", startIndex).takeIf { it != -1 }
                ?: text.length

            text.substring(startIndex, endIndex)
                .trim()
                .replace("**", "")
                .replace("*", "")
        } catch (e: Exception) {
            ""
        }
    }

    fun resetSuggestions() {
        _suggestions.value = emptyList()
        _hasResults.value = false
        _isLoading.value = false
        _errorMessage.value = ""
        _uploadedImage.value = null
        _occasion.value = ""
        _recipientInfo.value = ""
        _budget.value = ""
    }
}
