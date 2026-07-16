package com.sanchez.juarez.controllers.queries;

import com.juarez.domain.entities.views.CatalogView;
import com.juarez.domain.entities.views.ItemsView;
import com.sanchez.juarez.application.exceptions.QueryException;
import com.sanchez.juarez.application.queries.FindCatalogByTypeQuery;
import com.sanchez.juarez.application.queries.FindCatalogItemByCodeQuery;
import com.sanchez.juarez.application.queries.FindCatalogItemsByTypeQuery;
import com.sanchez.juarez.common.enums.CatalogType;
import com.sanchez.juarez.dtos.BaseResponseWrapper;
import com.sanchez.juarez.paths.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = ApiPaths.QUERIES_CATALOGS, version = "1")
@Tag(name = "Query Catalogs", description = "Endpoints de consulta para catálogos e ítems")
public class QueryCatalogControllerV1 {


    private static final Logger log = LoggerFactory.getLogger(QueryCatalogControllerV1.class);
    private final FindCatalogByTypeQuery findCatalogByTypeQuery;
    private final FindCatalogItemsByTypeQuery findCatalogItemsByTypeQuery;
    private final FindCatalogItemByCodeQuery findCatalogItemByCodeQuery;

    public QueryCatalogControllerV1(FindCatalogByTypeQuery findCatalogByTypeQuery,
                                    FindCatalogItemsByTypeQuery findCatalogItemsByTypeQuery,
                                    FindCatalogItemByCodeQuery findCatalogItemByCodeQuery) {
        this.findCatalogByTypeQuery = findCatalogByTypeQuery;
        this.findCatalogItemsByTypeQuery = findCatalogItemsByTypeQuery;
        this.findCatalogItemByCodeQuery = findCatalogItemByCodeQuery;
    }

    @Operation(summary = "Obtener catálogo por tipo", description = "Retorna el catálogo correspondiente al tipo indicado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo encontrado"),
            @ApiResponse(responseCode = "404", description = "Catálogo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(path = "/{type}")
    public ResponseEntity<BaseResponseWrapper<CatalogView>> getByType(
            @Parameter(description = "Tipo de catálogo (ej. CATEGORY, CURRENCY)", required = true, example = "CATEGORY")
            @PathVariable String type) {

        log.info("GET catalog by type: {}", type);

        CatalogType catalogType = CatalogType.valueOf(type.toUpperCase());

        CatalogView response = this.findCatalogByTypeQuery.execute(catalogType)
                .orElseThrow(() -> new QueryException("Catalog with type " + type + " not found"));

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener ítems de un catálogo por tipo", description = "Retorna todos los ítems del catálogo del tipo especificado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de ítems del catálogo")
    })
    @GetMapping(path = "/{type}/items")
    public ResponseEntity<BaseResponseWrapper<List<ItemsView>>> getItemsByType(
            @Parameter(description = "Tipo de catálogo (ej. CATEGORY, CURRENCY)", required = true, example = "CATEGORY")
            @PathVariable String type) {
        log.info("GET catalog items by type: {}", type);

        CatalogType catalogType = CatalogType.valueOf(type.toUpperCase());

        List<ItemsView> response = this.findCatalogItemsByTypeQuery.execute(catalogType);

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener ítem de catálogo por tipo y código", description = "Retorna un ítem específico del catálogo filtrando por tipo y código")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ítem encontrado"),
            @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @GetMapping(path = "/{type}/items", params = "code")
    public ResponseEntity<BaseResponseWrapper<ItemsView>> getItemByTypeAndCode(
            @Parameter(description = "Tipo de catálogo (ej. CATEGORY, CURRENCY)", required = true, example = "CATEGORY")
            @PathVariable String type,
            @Parameter(description = "Código del ítem dentro del catálogo", required = true, example = "ELECTRONICS")
            @RequestParam String code) {
        log.info("GET catalog item by type: {} and code: {}", type, code);

        CatalogType catalogType = CatalogType.valueOf(type.toUpperCase());

        ItemsView response = this.findCatalogItemByCodeQuery.execute(catalogType, code)
                .orElseThrow(() -> new QueryException("Item with code " + code + " not found for type " + type));

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }
}
