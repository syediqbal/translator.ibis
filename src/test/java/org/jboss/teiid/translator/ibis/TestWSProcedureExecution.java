package org.jboss.teiid.translator.ibis;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import javax.activation.DataSource;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.http.HTTPBinding;

import org.junit.Test;
import org.mockito.Mockito;
import org.teiid.cdk.CommandBuilder;
import org.teiid.dqp.internal.datamgr.RuntimeMetadataImpl;
import org.teiid.language.Call;
import org.teiid.metadata.MetadataFactory;
import org.teiid.metadata.Procedure;
import org.teiid.query.metadata.SystemMetadata;
import org.teiid.query.metadata.TransformationMetadata;
import org.teiid.query.unittest.RealMetadataFactory;
import org.teiid.resource.adapter.ws.WSConnectionImpl;
import org.teiid.resource.adapter.ws.WSManagedConnectionFactory;
import org.teiid.resource.spi.BasicConnectionFactory;
import org.teiid.translator.ExecutionContext;

public class TestWSProcedureExecution {
	@Test
	public void testRestProcedureCall() throws Exception {
		// WSExecutionFactory ef = new WSExecutionFactory();
		// MetadataFactory mf = new MetadataFactory("vdb", 1, "x",
		// SystemMetadata.getInstance().getRuntimeTypeMap(), new Properties(),
		// null);
		// ef.getMetadata(mf, null);
		// // ef.
		// Procedure p =
		// mf.getSchema().getProcedure(WSExecutionFactory.INVOKE_HTTP);
		// // p.
		// assertEquals(6, p.getParameters().size());
		// p.getParameters().remove(4);
		// p = mf.getSchema().getProcedure("invoke");
		// assertEquals(6, p.getParameters().size());
		// p.getParameters().remove(5);
		//
		// TransformationMetadata tm =
		// RealMetadataFactory.createTransformationMetadata(mf.asMetadataStore(),
		// "vdb");
		// RuntimeMetadataImpl rm = new RuntimeMetadataImpl(tm);

		// create connection
		WSManagedConnectionFactory wsmcf = new WSManagedConnectionFactory();

		wsmcf.setEndPoint("http://mockibis-1381187054851.rhcloud.com/rest/mockibis/v1/master/content");

		BasicConnectionFactory<WSConnectionImpl> cf = wsmcf
				.createConnectionFactory();
		WSConnectionImpl conn = cf.getConnection();

		conn.createDispatch(
				HTTPBinding.HTTP_BINDING,
				"http://mockibis-1381187054851.rhcloud.com/rest/mockibis/v1/master/content",
				DataSource.class, Mode.PAYLOAD);

		// WSConnection mockConnection = Mockito.mock(WSConnection.class);
		// Dispatch<Object> mockDispatch = mockDispatch();
		// Mockito.stub(mockDispatch.invoke(Mockito.any(DataSource.class))).toReturn(Mockito.mock(DataSource.class));
		// Mockito.stub(mockConnection.createDispatch(Mockito.any(String.class),
		// Mockito.any(String.class), Mockito.any(Class.class),
		// Mockito.any(Service.Mode.class))).toReturn(mockDispatch);
		// CommandBuilder cb = new CommandBuilder(tm);
		//
		// Call call =
		// (Call)cb.getCommand("call invokeHttp('GET', null, null)");
		// BinaryWSProcedureExecution pe = new BinaryWSProcedureExecution(call,
		// rm, Mockito.mock(ExecutionContext.class),ef, conn);
		// pe.execute();
		// // pe.
		// System.out.println(pe.getOutputParameterValues().toString());
		// pe.next();
		// mockConnection = Mockito.mock(WSConnection.class);
		// mockDispatch = Mockito.mock(Dispatch.class);
		// Mockito.stub(mockDispatch.invoke(Mockito.any(StAXSource.class))).toReturn(Mockito.mock(StAXSource.class));
		// Mockito.stub(mockConnection.createDispatch(Mockito.any(String.class),
		// Mockito.any(String.class), Mockito.any(Class.class),
		// Mockito.any(Service.Mode.class))).toReturn(mockDispatch);
		// call = (Call)cb.getCommand("call invoke()");
		// WSProcedureExecution wpe = new WSProcedureExecution(call, rm,
		// Mockito.mock(ExecutionContext.class), ef, mockConnection);
		// wpe.execute();
		// wpe.getOutputParameterValues();
	}
}
