package org.postgresql.test.unixsocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.postgresql.PGProperty;
import org.postgresql.core.SocketAddressFactory;
import org.postgresql.util.HostSpec;

import org.junit.Test;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.net.SocketFactory;





public class UnixSocketSimpleTest {

  @Test
  public void test() throws SQLException {
    Properties props = new Properties();
    props.setProperty(PGProperty.SOCKET_FACTORY.getName(),
        LocalPostgresSocketFactory.class.getName());
    props.setProperty(PGProperty.SOCKET_ADDRESS_FACTORY.getName(),
        LocalPostgresSocketAddressFactory.class.getName());

    try (Connection con = DriverManager.getConnection("jdbc:postgresql:sockettest", props);
        Statement st = con.createStatement();
        ResultSet res = st.executeQuery("select 1")) {
      assertTrue(res.next());
      assertEquals(1, res.getInt(1));
      assertFalse(res.next());
    }
  }

  public static class LocalPostgresSocketAddressFactory implements SocketAddressFactory {
    @Override
    public SocketAddress create(HostSpec hostSpec) {
      try {
        assertEquals("localhost", hostSpec.getHost());
        assertEquals(5432, hostSpec.getPort());
        return new AFUNIXSocketAddress(new File("/var/run/postgresql/.s.PGSQL.5432"));
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

  }

  public static class LocalPostgresSocketFactory extends SocketFactory {

    @Override
    public Socket createSocket() throws IOException {
      return AFUNIXSocket.newInstance();
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
      throw new RuntimeException();
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
      throw new RuntimeException();
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
        throws IOException, UnknownHostException {
      throw new RuntimeException();
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress,
        int localPort)
        throws IOException {
      throw new RuntimeException();
    }

  }

}
