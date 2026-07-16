package com.sanchez.juarez.controllers.queries;

import com.juarez.domain.entities.views.ProductView;
import com.sanchez.juarez.application.exceptions.QueryException;
import com.sanchez.juarez.application.queries.*;
import com.sanchez.juarez.dtos.BaseResponseWrapper;
import com.sanchez.juarez.paths.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = ApiPaths.QUERIES_PRODUCTS, version = "1")
@Tag(name = "Query Products", description = "Endpoints de consulta para productos")
public class QueryProductsControllersV1 {

    private static final Logger log = LoggerFactory.getLogger(QueryProductsControllersV1.class);
    private final FindProductByIdQuery findProductByIdQuery;
    private final FindProductBySkuQuery findProductBySkuQuery;
    private final FindProductActiveQuery findProductActiveQuery;
    private final FindProductByTextQuery findProductByTextQuery;
    private final FindProductByCategory findProductByCategory;

    public QueryProductsControllersV1(FindProductByIdQuery findProductByIdQuery,
                                      FindProductBySkuQuery findProductBySkuQuery,
                                      FindProductActiveQuery findProductActiveQuery,
                                      FindProductByTextQuery findProductByTextQuery,
                                      FindProductByCategory findProductByCategory) {
        this.findProductByIdQuery = findProductByIdQuery;
        this.findProductBySkuQuery = findProductBySkuQuery;
        this.findProductActiveQuery = findProductActiveQuery;
        this.findProductByTextQuery = findProductByTextQuery;
        this.findProductByCategory = findProductByCategory;
    }

    @Operation(summary = "Obtener producto por ID", description = "Retorna un producto dado su identificador único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<BaseResponseWrapper<ProductView>> getById(
            @Parameter(description = "Identificador único del producto", required = true, example = "abc123")
            @PathVariable String id) {

        log.info("GET product by id: {}", id);

        ProductView response = this.findProductByIdQuery.execute(id)
                .orElseThrow(() -> new QueryException("Product with id " + id + " not found"));

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener producto por SKU", description = "Retorna un producto dado su código SKU")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(params = "sku")
    public ResponseEntity<BaseResponseWrapper<ProductView>> getBySku(
            @Parameter(description = "Código SKU del producto", required = true, example = "SKU-001")
            @RequestParam String sku) {
        log.info("GET product by sku: {}", sku);

        ProductView response = this.findProductBySkuQuery.execute(sku)
                .orElseThrow(() -> new QueryException("Product with sku " + sku + " not found"));

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener productos activos", description = "Retorna la lista de todos los productos activos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos activos"),
            @ApiResponse(responseCode = "204", description = "No hay productos activos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(path = "/active")
    public ResponseEntity<BaseResponseWrapper<List<ProductView>>> getActive() {
        log.info("GET product Active");

        List<ProductView> response = this.findProductActiveQuery.execute();

        if (response.isEmpty()) {
            log.info("No active products found");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Buscar productos por texto", description = "Retorna productos cuyo nombre o descripción coincida con el texto de búsqueda")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados de búsqueda"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/search")
    public ResponseEntity<BaseResponseWrapper<List<ProductView>>> search(
            @Parameter(description = "Texto a buscar en nombre o descripción del producto", required = true, example = "laptop")
            @RequestParam String text) {
        log.info("GET search: {}", text);

        List<ProductView> products = this.findProductByTextQuery.execute(text);

        return ResponseEntity.ok(BaseResponseWrapper.of(products));
    }

    @Operation(summary = "Obtener productos por categoría", description = "Retorna todos los productos que pertenecen a la categoría indicada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos por categoría"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(params = "category")
    public ResponseEntity<BaseResponseWrapper<List<ProductView>>> findByCategory(
            @Parameter(description = "Identificador de la categoría a filtrar", required = true, example = "ELECTRONICS")
            @RequestParam String category) {
        log.info("GET findByCategory: {}", category);

        List<ProductView> products = this.findProductByCategory.execute(category);

        return ResponseEntity.ok(BaseResponseWrapper.of(products));
    }
}
