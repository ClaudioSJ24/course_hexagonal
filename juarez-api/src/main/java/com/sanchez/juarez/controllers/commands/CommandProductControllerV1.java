package com.sanchez.juarez.controllers.commands;

import com.sanchez.juarez.application.commands.product.CreateProductCommand;
import com.sanchez.juarez.application.commands.product.DesactivateProductCommand;
import com.sanchez.juarez.application.commands.product.UpdateProductCommand;
import com.sanchez.juarez.application.commands.product.UpdateStockCommand;
import com.sanchez.juarez.application.use_cases.product.CreateProductUseCase;
import com.sanchez.juarez.application.use_cases.product.DesactivateProductUseCase;
import com.sanchez.juarez.application.use_cases.product.UpdateProductUseCase;
import com.sanchez.juarez.application.use_cases.product.UpdateStockUseCase;
import com.sanchez.juarez.paths.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping(path = ApiPaths.COMMANDS_PRODUCTS, version = "1")
@Tag(name = "Command Products", description = "Endpoints de comandos para gestión de productos")
public class CommandProductControllerV1 {

    private static final Logger log = LoggerFactory.getLogger(CommandProductControllerV1.class);
    private final CreateProductUseCase createProductUseCase;
    private final DesactivateProductUseCase deactivateProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;

    public CommandProductControllerV1(CreateProductUseCase createProductUseCase,
                                      DesactivateProductUseCase deactivateProductUseCase,
                                      UpdateProductUseCase updateProductUseCase,
                                      UpdateStockUseCase updateStockUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.deactivateProductUseCase = deactivateProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.updateStockUseCase = updateStockUseCase;
    }

    @Operation(summary = "Crear producto", description = "Crea un nuevo producto con su información e imagen asociada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de producto inválidos"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio"),
            @ApiResponse(responseCode = "422", description = "Error al procesar el comando"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<Void> postProduct(
            @Parameter(description = "Datos del producto a crear", required = true)
            @Valid @RequestPart(value = "product") CreateProductCommand productCommandReq,
            @Parameter(description = "Imagen del producto", required = true)
            @RequestPart(value = "image") MultipartFile img) throws IOException {

        log.info("POST product");

        var command = new CreateProductCommand(
                productCommandReq.sku(),
                productCommandReq.name(),
                productCommandReq.description(),
                productCommandReq.price(),
                productCommandReq.currency(),
                productCommandReq.stock(),
                productCommandReq.categoryId(),
                img != null ? img.getBytes() : null,
                img != null ? img.getOriginalFilename() : null,
                productCommandReq.createdBy()
        );

        String productId = this.createProductUseCase.execute(command);

        return ResponseEntity.created(URI.create(ApiPaths.COMMANDS_PRODUCTS + "/" + productId)).build();

    }

    @Operation(summary = "Actualizar producto", description = "Actualiza la información e imagen de un producto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de producto inválidos"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio"),
            @ApiResponse(responseCode = "422", description = "Error al procesar el comando"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> putProduct(
            @Parameter(description = "Identificador único del producto", required = true, example = "abc123")
            @PathVariable String id,
            @Parameter(description = "Nuevos datos del producto", required = true)
            @Valid @RequestPart(value = "product") UpdateProductCommand productCommandReq,
            @Parameter(description = "Nueva imagen del producto", required = true)
            @RequestPart(value = "image") MultipartFile img) throws IOException {
        log.info("PUT product");

        var command = new UpdateProductCommand(
                id,
                productCommandReq.name(),
                productCommandReq.description(),
                productCommandReq.price(),
                productCommandReq.categoryId(),
                img != null ? img.getBytes() : null,
                img != null ? img.getOriginalFilename() : null
        );

        this.updateProductUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desactivar producto", description = "Marca un producto como inactivo dado su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto desactivado exitosamente"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio"),
            @ApiResponse(responseCode = "422", description = "Error al procesar el comando"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping(path = "/{id}/deactivate")
    public ResponseEntity<Void> patchProductDeactivate(
            @Parameter(description = "Identificador único del producto", required = true, example = "abc123")
            @PathVariable String id) {
        log.info("PATCH product deactivate");

        var command = new DesactivateProductCommand(id);

        this.deactivateProductUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar stock de producto", description = "Modifica la cantidad en inventario de un producto indicando el motivo del ajuste")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Stock actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de stock inválidos"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio"),
            @ApiResponse(responseCode = "422", description = "Error al procesar el comando"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping(path = "/{id}/stock")
    public ResponseEntity<Void> patchProductStock(
            @Parameter(description = "Identificador único del producto", required = true, example = "abc123")
            @PathVariable String id,
            @Parameter(description = "Comando con la cantidad y motivo del ajuste de stock", required = true)
            @Valid @RequestBody UpdateStockCommand stockCommand) {
        log.info("PATCH product stock");

        var command = new UpdateStockCommand(
                id,
                stockCommand.quantity(),
                stockCommand.reason()
        );

        this.updateStockUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}
