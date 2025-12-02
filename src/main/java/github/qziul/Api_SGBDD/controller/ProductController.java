package github.qziul.Api_SGBDD.controller;

import github.qziul.Api_SGBDD.entity.Product;
import github.qziul.Api_SGBDD.repository.ProductRepository;
import github.qziul.Api_SGBDD.service.ProductService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/products"})
public class ProductController {
    private final ProductService service;

    public ProductController(ProductRepository repository) {
        this.service = new ProductService(repository);
    }

    @PostMapping
    public ResponseEntity<Product> criar(@RequestBody Product produto) {
        return ResponseEntity.ok(this.service.salvar(produto));
    }

    @GetMapping
    public ResponseEntity<List<Product>> listar() {
        return ResponseEntity.ok(this.service.listarTodos());
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<Product> buscar(@PathVariable Long id) {
        return this.service.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<Product> atualizar(@PathVariable Long id, @RequestBody Product produto) {
        return ResponseEntity.ok(this.service.atualizar(id, produto));
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        this.service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}