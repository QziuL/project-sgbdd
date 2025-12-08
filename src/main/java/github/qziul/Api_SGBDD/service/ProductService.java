package github.qziul.Api_SGBDD.service;

import github.qziul.Api_SGBDD.entity.Product;
import github.qziul.Api_SGBDD.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = false)
    public Product salvar(Product product) {
        System.out.println("Operação de Escrita: Salvando no Master...");
        return this.repository.save(product);
    }

    @Transactional(readOnly = false)
    public Product atualizar(Long id, Product productUpdated) {
        Optional<Product> productExists = this.repository.findById(id);
        if (productExists.isPresent()) {
            Product product = productExists.get();
            product.setName(productUpdated.getName());
            product.setPrice(productUpdated.getPrice());
            product.setQuantity(productUpdated.getQuantity());
            return this.repository.save(product);
        } else {
            throw new RuntimeException("Produto não encontrado");
        }
    }

    @Transactional(readOnly = false)
    public void deletar(Long id) {
        this.repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> listarTodos() {
        System.out.println("Operação de Leitura: Buscando no Slave...");
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Product> buscarPorId(Long id) {
        return this.repository.findById(id);
    }
}