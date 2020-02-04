package com.geochat.geochat.web.rest;

import com.geochat.geochat.GeochatBackendApp;
import com.geochat.geochat.domain.Chat;
import com.geochat.geochat.repository.ChatRepository;
import com.geochat.geochat.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.geochat.geochat.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ChatResource} REST controller.
 */
@SpringBootTest(classes = GeochatBackendApp.class)
public class ChatResourceIT {

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_SRC_USER = "AAAAAAAAAA";
    private static final String UPDATED_SRC_USER = "BBBBBBBBBB";

    private static final String DEFAULT_DST_USER = "AAAAAAAAAA";
    private static final String UPDATED_DST_USER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restChatMockMvc;

    private Chat chat;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChatResource chatResource = new ChatResource(chatRepository);
        this.restChatMockMvc = MockMvcBuilders.standaloneSetup(chatResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chat createEntity() {
        Chat chat = new Chat()
            .message(DEFAULT_MESSAGE)
            .srcUser(DEFAULT_SRC_USER)
            .dstUser(DEFAULT_DST_USER)
            .date(DEFAULT_DATE);
        return chat;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chat createUpdatedEntity() {
        Chat chat = new Chat()
            .message(UPDATED_MESSAGE)
            .srcUser(UPDATED_SRC_USER)
            .dstUser(UPDATED_DST_USER)
            .date(UPDATED_DATE);
        return chat;
    }

    @BeforeEach
    public void initTest() {
        chatRepository.deleteAll();
        chat = createEntity();
    }

    @Test
    public void createChat() throws Exception {
        int databaseSizeBeforeCreate = chatRepository.findAll().size();

        // Create the Chat
        restChatMockMvc.perform(post("/api/chats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chat)))
            .andExpect(status().isCreated());

        // Validate the Chat in the database
        List<Chat> chatList = chatRepository.findAll();
        assertThat(chatList).hasSize(databaseSizeBeforeCreate + 1);
        Chat testChat = chatList.get(chatList.size() - 1);
        assertThat(testChat.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testChat.getSrcUser()).isEqualTo(DEFAULT_SRC_USER);
        assertThat(testChat.getDstUser()).isEqualTo(DEFAULT_DST_USER);
        assertThat(testChat.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    public void createChatWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = chatRepository.findAll().size();

        // Create the Chat with an existing ID
        chat.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restChatMockMvc.perform(post("/api/chats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chat)))
            .andExpect(status().isBadRequest());

        // Validate the Chat in the database
        List<Chat> chatList = chatRepository.findAll();
        assertThat(chatList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void getAllChats() throws Exception {
        // Initialize the database
        chatRepository.save(chat);

        // Get all the chatList
        restChatMockMvc.perform(get("/api/chats?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chat.getId())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].srcUser").value(hasItem(DEFAULT_SRC_USER)))
            .andExpect(jsonPath("$.[*].dstUser").value(hasItem(DEFAULT_DST_USER)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }
    
    @Test
    public void getChat() throws Exception {
        // Initialize the database
        chatRepository.save(chat);

        // Get the chat
        restChatMockMvc.perform(get("/api/chats/{id}", chat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(chat.getId()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.srcUser").value(DEFAULT_SRC_USER))
            .andExpect(jsonPath("$.dstUser").value(DEFAULT_DST_USER))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    public void getNonExistingChat() throws Exception {
        // Get the chat
        restChatMockMvc.perform(get("/api/chats/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateChat() throws Exception {
        // Initialize the database
        chatRepository.save(chat);

        int databaseSizeBeforeUpdate = chatRepository.findAll().size();

        // Update the chat
        Chat updatedChat = chatRepository.findById(chat.getId()).get();
        updatedChat
            .message(UPDATED_MESSAGE)
            .srcUser(UPDATED_SRC_USER)
            .dstUser(UPDATED_DST_USER)
            .date(UPDATED_DATE);

        restChatMockMvc.perform(put("/api/chats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedChat)))
            .andExpect(status().isOk());

        // Validate the Chat in the database
        List<Chat> chatList = chatRepository.findAll();
        assertThat(chatList).hasSize(databaseSizeBeforeUpdate);
        Chat testChat = chatList.get(chatList.size() - 1);
        assertThat(testChat.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testChat.getSrcUser()).isEqualTo(UPDATED_SRC_USER);
        assertThat(testChat.getDstUser()).isEqualTo(UPDATED_DST_USER);
        assertThat(testChat.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    public void updateNonExistingChat() throws Exception {
        int databaseSizeBeforeUpdate = chatRepository.findAll().size();

        // Create the Chat

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatMockMvc.perform(put("/api/chats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chat)))
            .andExpect(status().isBadRequest());

        // Validate the Chat in the database
        List<Chat> chatList = chatRepository.findAll();
        assertThat(chatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteChat() throws Exception {
        // Initialize the database
        chatRepository.save(chat);

        int databaseSizeBeforeDelete = chatRepository.findAll().size();

        // Delete the chat
        restChatMockMvc.perform(delete("/api/chats/{id}", chat.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Chat> chatList = chatRepository.findAll();
        assertThat(chatList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
