package jyrs.dev.vivesbank.products.base.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.products.base.dto.ProductDto;
import jyrs.dev.vivesbank.products.base.dto.ProductUpdatedDto;
import jyrs.dev.vivesbank.products.base.exceptions.ProductExistingException;
import jyrs.dev.vivesbank.products.base.exceptions.ProductNotFoundException;
import jyrs.dev.vivesbank.products.base.models.Product;
import jyrs.dev.vivesbank.products.base.models.type.ProductType;
import jyrs.dev.vivesbank.products.base.repositories.ProductRepository;
import jyrs.dev.vivesbank.products.base.services.ProductServices;
import jyrs.dev.vivesbank.utils.pagination.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
class ProductControllerTest {
    private final String myEndpoint = "/vivesbank/v1/products";

    private final Product product = Product.builder()
            .id(1L)
            .type(ProductType.BANK_ACCOUNT)
            .specification("test_account")
            .tae(0.2)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    private final Product product2 = Product.builder()
            .id(2l)
            .type(ProductType.CREDIT_CARD)
            .specification("test_card")
            .tae(0.)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private ProductRepository productRepository;
    @Autowired
    @MockBean
    private ProductServices productServices;
    @Autowired
    private JacksonTester<ProductDto> jsonProductDto;
    @Autowired
    private JacksonTester<ProductUpdatedDto> jsonProductUpdatedDto;
    @Autowired
    public ProductControllerTest (ProductServices productServices){
        this.productServices = productServices;
        mapper.registerModule(new JavaTimeModule());
    }
    @Test
    void findAll() throws Exception {
        var productList = List.of(product, product2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(productList);

        when(productServices.getAll(Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                get(myEndpoint)
                       .accept(MediaType.APPLICATION_JSON))
               .andReturn().getResponse();
        PageResponse<Product> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){
                });

        assertAll("findAll",
                ()-> assertEquals(200, response.getStatus()),
                () -> assertEquals(productList, res.content())
        );

        verify(productServices, times(1)).getAll(Optional.empty(), pageable);
    }

    @Test
    void findAllByType() throws Exception {
    var productList = List.of(product);
    var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    var page = new PageImpl<>(productList);
    var type = ProductType.BANK_ACCOUNT;
    when(productServices.getByType(type, pageable)).thenReturn(page);
    MockHttpServletResponse response = mockMvc.perform(
            get(myEndpoint + "/type/" + type.name())
                   .accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();
    PageResponse<Product> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){
        });
    assertAll("findAllByType",
            () -> assertEquals(200, response.getStatus()),
            () -> assertEquals(productList, res.content())
    );
    verify(productServices, times(1)).getByType(type, pageable);
    }


    @Test
    void getById() throws Exception {
        var myLocalEndpoint = myEndpoint + "/id/1";

        when(productServices.getById(anyLong())).thenReturn(product);

        MockHttpServletResponse response = mockMvc.perform(
                get(myLocalEndpoint)
                       .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Product res = mapper.readValue(response.getContentAsString(), Product.class);

        assertAll("getById",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(product, res)
        );
        verify(productServices, times(1)).getById(anyLong());
    }

    @Test
    void getByIdNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/id/1";

        doThrow(new ProductNotFoundException(1L)).when(productServices).getById(1L);

        MockHttpServletResponse response = mockMvc.perform(
                get(myLocalEndpoint)
                       .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll("getByIdNotFound",
                () -> assertEquals(404, response.getStatus()),
                () -> assertEquals(response.getContentAsString(), ("Producto con id: 1 no encontrado"))
        );

        verify(productServices, times(1)).getById(anyLong());
    }

    @Test
    void getByType() throws Exception {
        var myLocalEndpoint = myEndpoint + "/type/CREDIT_CARD";

        when(productServices.getByType(ProductType.CREDIT_CARD, PageRequest.of(0, 10, Sort.by("id").ascending()))).thenReturn(new PageImpl<>(List.of(product)));

        MockHttpServletResponse response = mockMvc.perform(
                get(myLocalEndpoint)
                       .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<Product> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){
        });

        assertAll("getByType",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(List.of(product), res.content())
        );
    }

    @Test
    void create() throws Exception {

        ProductDto productCreatedDto = ProductDto.builder()
               .productType(ProductType.CREDIT_CARD)
               .specification("test_card")
               .tae(0.0)
               .build();

        when(productServices.save(any(ProductDto.class))).thenReturn(product);

        MockHttpServletResponse response = mockMvc.perform(
                post(myEndpoint)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(jsonProductDto.write(productCreatedDto).getJson())
                        .accept(MediaType.APPLICATION_JSON))
               .andReturn().getResponse();

        Product res = mapper.readValue(response.getContentAsString(), Product.class);

        assertAll("create",
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(product, res)
        );
    }

    @Test
    void createBadRequest() throws Exception {

        ProductDto productCreatedDto = ProductDto.builder()
                .productType(ProductType.CREDIT_CARD)
                .specification("test_card")
                .tae(0.0)
                .build();

        when(productServices.save(productCreatedDto)).thenThrow(new ProductExistingException(productCreatedDto.getSpecification()));
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonProductDto.write(productCreatedDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertEquals(response.getContentAsString(), ("Producto: "+productCreatedDto.getSpecification()+" ya existente."))
        );
    }

    @Test
    void update() throws Exception {

        var myLocalEndPoint = myEndpoint+"/1";

        var dto = ProductUpdatedDto.builder()
                .tipo(ProductType.CREDIT_CARD)
                .specification("updated_card")
                .tae(0.5)
                .build();

        when(productServices.update(any(ProductUpdatedDto.class), anyLong())).thenReturn(product);

        MockHttpServletResponse response = mockMvc.perform(
                put(myLocalEndPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProductUpdatedDto.write(dto).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();

        Product res = mapper.readValue(response.getContentAsString(), Product.class);

        assertAll("update",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(product, res)
        );
    }

    @Test
    void updateNotFound() throws Exception {

        var myLocalEndPoint= myEndpoint+"/1";

        var dto = ProductUpdatedDto.builder()
                .tipo(ProductType.CREDIT_CARD)
                .specification("updated_card")
                .tae(0.5)
                .build();

        doThrow(new ProductNotFoundException(1L)).when(productServices).update(any(ProductUpdatedDto.class), anyLong());

        MockHttpServletResponse response = mockMvc.perform(
                put(myLocalEndPoint)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(jsonProductUpdatedDto.write(dto).getJson())
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll("updateNotFound",
                () -> assertEquals(404, response.getStatus()),
                () -> assertEquals(response.getContentAsString(), ("Producto con id: 1 no encontrado"))
        );
    }

    @Test
    void deleteById() throws Exception {
        var myLocalEndPoint = myEndpoint+"/1";

        doNothing().when(productServices).deleteById(anyLong());

        mockMvc.perform(delete(myLocalEndPoint))
               .andExpect(status().isNoContent());

        verify(productServices, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteByIdNotFound () throws Exception {
         var myLocalEndPoint = myEndpoint+"/1";

        doThrow(new ProductNotFoundException(1L)).when(productServices).deleteById(anyLong());

        mockMvc.perform(delete(myLocalEndPoint))
                .andExpect(status().isNotFound());

        verify(productServices, times(1)).deleteById(anyLong());
    }
}