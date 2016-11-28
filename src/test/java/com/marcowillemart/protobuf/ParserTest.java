package com.marcowillemart.protobuf;

import com.marcowillemart.protobuf.parser.ProtobufLexer;
import com.marcowillemart.protobuf.parser.ProtobufParser;
import java.io.IOException;
import java.util.BitSet;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Protobuf parser.
 *
 * @author mwi
 */
public class ParserTest {

    private ProtobufParser target;

    private TestErrorListener errorListener;

    @Before
    public void setUp() {
        errorListener = new TestErrorListener();
    }

    @After
    public void tearDown() {
        assertTrue(errorListener.isValid());
    }

    @Test
    public void testExample() throws Exception {
        // Setup
        target = createParser("example", errorListener);

        // Exercise
        target.proto();
    }

    @Test
    public void testAddressbook() {
        // Setup
        target = createParser("addressbook", errorListener);

        // Exercise
        target.proto();
    }

    @Test
    public void testConformance() {
        // Setup
        target = createParser("conformance", errorListener);

        // Exercise
        target.proto();
    }

    ////////////////////
    // HELPER METHODS
    ////////////////////

    /**
     * @requires protoFileName != null && errorListener != null &&
     *           there exists a .proto file on the classpath at
     *           protoFileName + ".proto"
     * @return a new Protobuf parser for the .proto file named protoFileName and
     *         with the given error listener
     */
    private static ProtobufParser createParser(
            String protoFileName,
            ANTLRErrorListener errorListener) {

        CharStream input =
                createCharStream(
                        String.format("%s.proto", protoFileName));
        ProtobufLexer lexer = new ProtobufLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ProtobufParser parser = new ProtobufParser(tokens);
        parser.addErrorListener(errorListener);
        return parser;
    }

    /**
     * @requires protoFilePath != null &&
     *           there exists a .proto file on the classpath at protoFilePath
     * @return a new char stream for the .proto file on the classpath at
     *         'protoFilePath'
     */
    private static CharStream createCharStream(String protoFilePath) {
        try {
            return new ANTLRInputStream(
                    ClassLoader.getSystemResourceAsStream(protoFilePath));
        } catch (IOException ex) {
            throw new IllegalArgumentException("ParserTest.charStreamFrom", ex);
        }
    }

    ////////////////////
    // INNER CLASSES
    ////////////////////

    /**
     * Implementation of the ANTLRErrorListener that fails as soon as a syntax
     * error occurs.
     */
    private static class TestErrorListener implements ANTLRErrorListener {

        private boolean valid;

        TestErrorListener() {
            this.valid = true;
        }

        boolean isValid() {
            return valid;
        }

        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException ex) {

            valid = false;
        }

        @Override
        public void reportAmbiguity(
                Parser recognizer,
                DFA dfa,
                int startIndex,
                int stopIndex,
                boolean exact,
                BitSet ambigAlts,
                ATNConfigSet configs) {

            valid = false;
        }

        @Override
        public void reportAttemptingFullContext(
                Parser recognizer,
                DFA dfa,
                int startIndex,
                int stopIndex,
                BitSet conflictingAlts,
                ATNConfigSet configs) {

            valid = false;
        }

        @Override
        public void reportContextSensitivity(
                Parser recognizer,
                DFA dfa,
                int startIndex,
                int stopIndex,
                int prediction,
                ATNConfigSet configs) {

            valid = false;
        }
    } // end TestErrorListener
}
