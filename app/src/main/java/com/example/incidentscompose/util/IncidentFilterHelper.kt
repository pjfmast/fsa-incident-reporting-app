package com.example.incidentscompose.util

import com.example.incidentscompose.data.model.IncidentCategory
import com.example.incidentscompose.data.model.IncidentResponse
import com.example.incidentscompose.data.model.Priority
import com.example.incidentscompose.data.model.Status

object IncidentFilterHelper {

    fun filterIncidents(
        incidents: List<IncidentResponse>,
        searchQuery: String = "",
        priorityFilter: Set<Priority> = emptySet(),
        statusFilter: Set<Status> = emptySet(),
        categoryFilter: Set<IncidentCategory> = emptySet()
    ): List<IncidentResponse> {

        return incidents.filter { incident ->
            val matchesSearch = searchQuery.isBlank() ||
                    incident.category.name.contains(searchQuery, ignoreCase = true) ||
                    incident.description.contains(searchQuery, ignoreCase = true) ||
                    incident.priority.name.contains(searchQuery, ignoreCase = true) ||
                    incident.status.name.contains(searchQuery, ignoreCase = true)

            val matchesPriority = priorityFilter.isEmpty() ||
                    priorityFilter.contains(incident.priority)

            val matchesStatus = statusFilter.isEmpty() ||
                    statusFilter.contains(incident.status)

            val matchesCategory = categoryFilter.isEmpty() ||
                    categoryFilter.contains(incident.category)

            matchesSearch && matchesPriority && matchesStatus && matchesCategory
        }
    }

    fun hasActiveFilters(
        searchQuery: String = "",
        priorityFilter: Set<Priority> = emptySet(),
        statusFilter: Set<Status> = emptySet(),
        categoryFilter: Set<IncidentCategory> = emptySet()
    ): Boolean {
        return searchQuery.isNotEmpty() ||
                priorityFilter.isNotEmpty() ||
                statusFilter.isNotEmpty() ||
                categoryFilter.isNotEmpty()
    }
}
