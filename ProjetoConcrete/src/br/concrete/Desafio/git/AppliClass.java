package br.concrete.Desafio.git;

import static org.junit.Assert.assertNotNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;

import java.nio.charset.Charset;

import java.util.Arrays;

import org.junit.Before;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.http.converter.HttpMessageConverter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.mock.http.MockHttpOutputMessage;

import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)

@SpringBootTest

@AutoConfigureMockMvc

public class AppliClass {
	
	private MediaType contentType = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8"));

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private HttpMessageConverter mappingJackson2HttpMessageConverter;
	
	@Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);
        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void invalidCadastroVerbReturnMessage() throws Exception {
		mockMvc.perform(get("/cadastro")).andDo(print()).andExpect(status().isConflict()).andExpect(jsonPath("$.content").value("Sem 'nome' do usuário!"));
	}

	@Test
	public void invalidCadastroReturnSemDados() throws Exception {
		mockMvc.perform(post("/cadastro")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content").value("Sem dados do usuário!"));
	}

	@Test
	public void validCadastroReturnUserInfo() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("{ ");
		sb.append("	\"nome\": \"João Ninguém\", ");
		sb.append("	\"email\": \"joao@ninguem.org\", ");
		sb.append("	\"senha\": \"Test123\", ");
		sb.append("	\"telefones\": [ ");
		sb.append("		{	\"numero\": \"999001234\", ");
		sb.append("			\"ddd\": \"61\", ");
		sb.append("			\"id\": null ");
		sb.append("		}, ");
		sb.append("		{	\"numero\": \"33221234\", ");
		sb.append("			\"ddd\": \"61\", ");
		sb.append("			\"id\": null ");
		sb.append("		} ");
		sb.append("	] ");
		sb.append("} ");
		//String usuarioJson = json(usuario);
		String usuarioJson = sb.toString();
		this.mockMvc.perform(post("/cadastro")
				.contentType(contentType)
				.content(usuarioJson))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.content").value("joao@ninguem.org"));
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(
				o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}