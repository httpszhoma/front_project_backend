package zhoma.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhoma.dto.BasketItemDto;
import zhoma.dto.BasketResponseDto;
import zhoma.dto.ProductQuantityUpdateDto;
import zhoma.models.Basket;
import zhoma.models.BasketItem;
import zhoma.models.Product;
import zhoma.models.User;
import zhoma.repository.BasketRepository;
import zhoma.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BasketService {

    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;

    public BasketService(BasketRepository basketRepository, ProductRepository productRepository) {
        this.basketRepository = basketRepository;
        this.productRepository = productRepository;
    }

    public Basket getBasketForUser(User user) {
        return basketRepository.findByUser(user)
                .orElseGet(() -> {
                    Basket basket = new Basket();
                    basket.setUser(user);
                    return basketRepository.save(basket);
                });
    }

    @Transactional
    public void addProductToBasket(User user, Long productId, int quantity) {
        Basket basket = getBasketForUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        BasketItem item = basket.getItems().stream()
                .filter(basketItem -> basketItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item == null) {
            item = new BasketItem();
            item.setBasket(basket);
            item.setProduct(product);
            basket.getItems().add(item);
        }

        item.setQuantity(item.getQuantity() + quantity);
        basketRepository.save(basket);
    }

    @Transactional
    public void removeProductFromBasket(User user, Long productId) {
        Basket basket = getBasketForUser(user);
        BasketItem item = basket.getItems().stream()
                .filter(basketItem -> basketItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in basket"));

        basket.getItems().remove(item);
        basketRepository.save(basket);
    }

    @Transactional
    public void updateProductQuantities(User user, List<ProductQuantityUpdateDto> updates) {
        Basket basket = getBasketForUser(user);

        for (ProductQuantityUpdateDto update : updates) {
            BasketItem item = basket.getItems().stream()
                    .filter(basketItem -> basketItem.getProduct().getId().equals(update.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Product not found in basket"));

            item.setQuantity(update.getQuantity());
        }

        basketRepository.save(basket);
    }


    public BasketResponseDto convertBasketToDto(Basket basket) {
        BasketResponseDto basketResponseDto = new BasketResponseDto();

        List<BasketItemDto> itemDtos = basket.getItems().stream()
                .map(this::convertBasketItemToDto)
                .collect(Collectors.toList());

        basketResponseDto.setUserId(basket.getUser().getId());
        basketResponseDto.setItems(itemDtos);
        basketResponseDto.setTotalPrice(basket.calculateTotalPrice());

        return basketResponseDto;
    }

    private BasketItemDto convertBasketItemToDto(BasketItem basketItem) {
        BasketItemDto itemDto = new BasketItemDto();
        itemDto.setProductId(basketItem.getProduct().getId());
        itemDto.setProductName(basketItem.getProduct().getName());
        itemDto.setQuantity(basketItem.getQuantity());
        itemDto.setPrice(basketItem.getProduct().getPrice());
        itemDto.setTotalPrice(basketItem.getProduct().getPrice() * basketItem.getQuantity());

        itemDto.setBrand(basketItem.getProduct().getBrandEntity().getName());
        itemDto.setProductImage(basketItem.getProduct().getImages().isEmpty() ? null : basketItem.getProduct().getImages().get(0).getImageUrl()); // Берем первое изображение

        return itemDto;
    }

}
