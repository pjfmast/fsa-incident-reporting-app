package com.example.incidentscompose.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.incidentscompose.data.model.ApiResult
import com.example.incidentscompose.data.model.IncidentCategory
import com.example.incidentscompose.data.model.IncidentResponse
import com.example.incidentscompose.data.model.Priority
import com.example.incidentscompose.data.model.Role
import com.example.incidentscompose.data.model.Status
import com.example.incidentscompose.data.repository.IncidentRepository
import com.example.incidentscompose.data.store.TokenPreferences
import com.example.incidentscompose.util.IncidentFilterHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IncidentManagementUiState(
    val userRole: Role? = null,
    val allIncidents: List<IncidentResponse> = emptyList(),
    val displayedIncidents: List<IncidentResponse> = emptyList(),
    val unauthorizedState: Boolean = false,
    val showLoadMore: Boolean = false,
    val toastMessage: String? = null,
    val searchQuery: String = "",
    val selectedPriorityFilter: Set<Priority> = emptySet(),
    val selectedStatusFilter: Set<Status> = emptySet(),
    val selectedCategoryFilter: Set<IncidentCategory> = emptySet(),
    val filteredIncidents: List<IncidentResponse> = emptyList()
)

class IncidentManagementViewModel(
    private val incidentRepository: IncidentRepository,
    private val tokenPreferences: TokenPreferences
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(IncidentManagementUiState())
    val uiState: StateFlow<IncidentManagementUiState> = _uiState.asStateFlow()

    private var currentDisplayCount = 0
    private val pageSize = 20

    init {
        loadUserRole()
        getAllIncidents()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                userRole = tokenPreferences.getUserRole()
            )
        }
    }

    fun getAllIncidents() {
        viewModelScope.launch {
            withLoading {
                when (val result = incidentRepository.getAllIncidents()) {
                    is ApiResult.Success -> {
                        val incidents = result.data
                        currentDisplayCount = minOf(pageSize, incidents.size)
                        _uiState.value = _uiState.value.copy(
                            allIncidents = incidents,
                            displayedIncidents = incidents.take(currentDisplayCount),
                            showLoadMore = incidents.size > currentDisplayCount,
                            toastMessage = null
                        )
                        applyFilters()
                    }

                    is ApiResult.Unauthorized -> _uiState.value = _uiState.value.copy(unauthorizedState = true)

                    is ApiResult.HttpError -> _uiState.value = _uiState.value.copy(
                        toastMessage = "Failed to load incidents: ${result.message}"
                    )

                    is ApiResult.NetworkError -> _uiState.value = _uiState.value.copy(
                        toastMessage = "Network error: ${result.exception.message}"
                    )

                    is ApiResult.Timeout -> _uiState.value = _uiState.value.copy(
                        toastMessage = "Request timed out while loading incidents."
                    )

                    is ApiResult.Unknown -> _uiState.value = _uiState.value.copy(
                        toastMessage = "An unexpected error occurred while loading incidents."
                    )
                }
            }
        }
    }

    fun deleteIncident(incidentId: Long) {
        viewModelScope.launch {
            when (val result = incidentRepository.deleteIncident(incidentId)) {
                is ApiResult.Success -> {
                    val updatedList = _uiState.value.allIncidents.filterNot { it.id == incidentId }

                    // Update display after deletion
                    currentDisplayCount = minOf(currentDisplayCount, updatedList.size)
                    _uiState.value = _uiState.value.copy(
                        allIncidents = updatedList,
                        displayedIncidents = updatedList.take(currentDisplayCount),
                        toastMessage = "Incident deleted successfully"
                    )

                    applyFilters()
                }

                is ApiResult.Unauthorized -> _uiState.value = _uiState.value.copy(unauthorizedState = true)

                is ApiResult.HttpError -> {
                    _uiState.value = _uiState.value.copy(
                        toastMessage = "Failed to delete incident: ${result.message}"
                    )
                }

                is ApiResult.NetworkError -> {
                    _uiState.value = _uiState.value.copy(
                        toastMessage = "Network error: ${result.exception.message}"
                    )
                }

                is ApiResult.Timeout -> {
                    _uiState.value = _uiState.value.copy(
                        toastMessage = "Delete request timed out"
                    )
                }

                is ApiResult.Unknown -> {
                    _uiState.value = _uiState.value.copy(
                        toastMessage = "Unexpected error occurred"
                    )
                }
            }
        }
    }

    fun loadMoreIncidents() {
        val allIncidents = _uiState.value.allIncidents
        val newDisplayCount = minOf(currentDisplayCount + pageSize, allIncidents.size)

        if (newDisplayCount > currentDisplayCount) {
            _uiState.value = _uiState.value.copy(
                displayedIncidents = allIncidents.take(newDisplayCount),
                showLoadMore = allIncidents.size > newDisplayCount
            )
            currentDisplayCount = newDisplayCount
            applyFilters()
        } else {
            _uiState.value = _uiState.value.copy(showLoadMore = false)
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        val filtered = IncidentFilterHelper.filterIncidents(
            incidents = state.displayedIncidents,
            searchQuery = state.searchQuery,
            priorityFilter = state.selectedPriorityFilter,
            statusFilter = state.selectedStatusFilter,
            categoryFilter = state.selectedCategoryFilter
        )
        _uiState.value = state.copy(filteredIncidents = filtered)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun updatePriorityFilter(priorities: Set<Priority>) {
        _uiState.value = _uiState.value.copy(selectedPriorityFilter = priorities)
        applyFilters()
    }

    fun updateStatusFilter(statuses: Set<Status>) {
        _uiState.value = _uiState.value.copy(selectedStatusFilter = statuses)
        applyFilters()
    }

    fun updateCategoryFilter(categories: Set<IncidentCategory>) {
        _uiState.value = _uiState.value.copy(selectedCategoryFilter = categories)
        applyFilters()
    }

    fun clearAllFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedPriorityFilter = emptySet(),
            selectedStatusFilter = emptySet(),
            selectedCategoryFilter = emptySet()
        )
        applyFilters()
    }

    val hasActiveFilters: Boolean
        get() = IncidentFilterHelper.hasActiveFilters(
            searchQuery = _uiState.value.searchQuery,
            priorityFilter = _uiState.value.selectedPriorityFilter,
            statusFilter = _uiState.value.selectedStatusFilter,
            categoryFilter = _uiState.value.selectedCategoryFilter
        )

    fun refreshIncidents() {
        currentDisplayCount = 0
        getAllIncidents()
    }
}
