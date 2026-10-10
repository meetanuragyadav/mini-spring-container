
package com.minispring.aop;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProxyCheckedExceptionTest {

    interface FileService {
        String readFile() throws IOException;
    }

    static class FileServiceImpl implements FileService {
        @Override
        public String readFile() throws IOException {
            throw new IOException("disk unavailable");
        }
    }

    @Test
    void shouldPropagateDeclaredCheckedException() {
        MethodExecution execution = new MethodExecution(List.of());
        MiniProxyFactory factory = new MiniProxyFactory(execution);

        FileService service = factory.createProxy(
                new FileServiceImpl(), FileService.class);

        IOException exception = assertThrows(
                IOException.class,
                service::readFile);

        assertEquals("disk unavailable", exception.getMessage());
    }
}
