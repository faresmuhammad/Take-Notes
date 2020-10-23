package com.fares.training.takenotes.util

import kotlinx.coroutines.flow.*

inline fun <DataTypeFromDatabase, RequestTypeFromApi> networkBoundResource(
    crossinline query: () -> Flow<DataTypeFromDatabase>,
    crossinline fetch: suspend () -> RequestTypeFromApi,
    crossinline saveFetchedResult: suspend (RequestTypeFromApi) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = {},
    crossinline shouldFetch: (DataTypeFromDatabase) -> Boolean = { true }
) = flow {
    emit(Resource.Loading(null))
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))
        try {
            saveFetchedResult(fetch())
            query().map { Resource.Success(it) }

        } catch (t: Throwable) {
            onFetchFailed(t)
            query().map {
                Resource.Error("Couldn't reach server. It might be down", data)
            }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}