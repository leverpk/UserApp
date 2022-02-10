package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SimpleUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SimpleUser.class);
        SimpleUser simpleUser1 = new SimpleUser();
        simpleUser1.setId(1L);
        SimpleUser simpleUser2 = new SimpleUser();
        simpleUser2.setId(simpleUser1.getId());
        assertThat(simpleUser1).isEqualTo(simpleUser2);
        simpleUser2.setId(2L);
        assertThat(simpleUser1).isNotEqualTo(simpleUser2);
        simpleUser1.setId(null);
        assertThat(simpleUser1).isNotEqualTo(simpleUser2);
    }
}
