package org.piercecountywa.pac.security

import grails.plugin.springsecurity.SecurityFilterPosition
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
import org.apache.velocity.app.VelocityEngine
import org.opensaml.saml2.metadata.provider.DOMMetadataProvider
import org.opensaml.saml2.metadata.provider.MetadataProvider
import org.opensaml.saml2.metadata.provider.MetadataProviderException
import org.opensaml.util.resource.ClasspathResource
import org.opensaml.xml.parse.ParserPool
import org.opensaml.xml.parse.StaticBasicParserPool
import org.piercecountywa.pac.core.Settings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.saml.*
import org.springframework.security.saml.context.SAMLContextProviderImpl
import org.springframework.security.saml.context.SAMLContextProviderLB
import org.springframework.security.saml.key.JKSKeyManager
import org.springframework.security.saml.key.KeyManager
import org.springframework.security.saml.log.SAMLDefaultLogger
import org.springframework.security.saml.metadata.*
import org.springframework.security.saml.parser.ParserPoolHolder
import org.springframework.security.saml.processor.*
import org.springframework.security.saml.util.VelocityFactory
import org.springframework.security.saml.websso.*
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.w3c.dom.Document
import org.w3c.dom.Element

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

@Conditional(SamlSecurityCondition)
@Configuration
@EnableWebSecurity
class SamlSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private Timer backgroundTaskTimer

    private MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager

    @Value("#{systemProperties[SAML_PASSWORD_PROTECTED_TRANSPORT] ?:  false}")
    private boolean usePasswordProtectedTransport;

    @PostConstruct
    void init() {
      this.backgroundTaskTimer = new Timer(true)
      this.multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager()
    }

    @PreDestroy
    void destroy() {
      this.backgroundTaskTimer.purge()
      this.backgroundTaskTimer.cancel()
      this.multiThreadedHttpConnectionManager.shutdown()
    }

    @Autowired
    private SAMLUserDetailsServiceImpl samlUserDetailsServiceImpl

    @Bean
    VelocityEngine velocityEngine() {
      return VelocityFactory.getEngine()
    }

    @Bean(initMethod = "initialize")
    StaticBasicParserPool parserPool() {
      return new StaticBasicParserPool()
    }

    @Bean(name = "parserPoolHolder")
    ParserPoolHolder parserPoolHolder() {
      return new ParserPoolHolder()
    }

    @Bean
    HttpClient httpClient() {
      return new HttpClient(this.multiThreadedHttpConnectionManager)
    }

    @Bean
    SAMLAuthenticationProvider samlAuthenticationProvider() {
      SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider()
      samlAuthenticationProvider.setUserDetails(samlUserDetailsServiceImpl)
      samlAuthenticationProvider.setForcePrincipalAsString(false)
      return samlAuthenticationProvider
    }

    @Bean
    SAMLContextProviderImpl contextProvider() {
      SAMLContextProviderLB contextProvider = new SAMLContextProviderLB()
      contextProvider.setScheme("https")
      contextProvider.setServerName(Settings.serverName)
      contextProvider.setContextPath(Settings.contextPath)
      contextProvider
    }

    @Bean
    static SAMLBootstrap sAMLBootstrap() {
      return new SAMLBootstrap()
    }

    @Bean
    SAMLDefaultLogger samlLogger() {
      return new SAMLDefaultLogger()
    }

    @Bean
    WebSSOProfileConsumer webSSOprofileConsumer() {
      WebSSOProfileConsumerImpl profileConsumer = new WebSSOProfileConsumerImpl()
      profileConsumer.setResponseSkew(300)
      profileConsumer.setMaxAuthenticationAge(604800)
      profileConsumer.setMaxAssertionTime(3000)
      return profileConsumer
    }

    @Bean
    WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
      return new WebSSOProfileConsumerHoKImpl()
    }

    @Bean
    WebSSOProfile webSSOprofile() {
      return new WebSSOProfileImpl()
    }

    @Bean
    WebSSOProfileConsumerHoKImpl hokWebSSOProfile() {
      return new WebSSOProfileConsumerHoKImpl()
    }

    @Bean
    WebSSOProfileECPImpl ecpprofile() {
      return new WebSSOProfileECPImpl()
    }

    @Bean
    SingleLogoutProfile logoutprofile() {
      return new SingleLogoutProfileImpl()
    }

    @Bean
    KeyManager keyManager() {
      DefaultResourceLoader loader = new DefaultResourceLoader()
      Resource storeFile = loader.getResource("classpath:resources/saml/keystore.jks")
      String storePass = "nalle123"
      Map<String, String> passwords = new HashMap<String, String>()
      passwords.put("ping", "ping123")
      String defaultKey = "ping"
      return new JKSKeyManager(storeFile, storePass, passwords, defaultKey)
    }

    // Setup TLS Socket Factory
    //  @Bean
    //  TLSProtocolConfigurer tlsProtocolConfigurer() {
    //    return new TLSProtocolConfigurer()
    //  }

    //  @Bean
    //  ProtocolSocketFactory socketFactory() {
    //    return new TLSProtocolSocketFactory(keyManager(), null, "default")
    //  }

    //  @Bean
    //  Protocol socketFactoryProtocol() {
    //    return new Protocol("https", socketFactory(), 443)
    //  }

    //  @Bean
    //  MethodInvokingFactoryBean socketFactoryInitialization() {
    //    MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean()
    //    methodInvokingFactoryBean.setTargetClass(Protocol.class)
    //    methodInvokingFactoryBean.setTargetMethod("registerProtocol")
    //    Object[] args = {"https", socketFactoryProtocol()}
    //    methodInvokingFactoryBean.setArguments(args)
    //    return methodInvokingFactoryBean
    //  }

    @Bean
    WebSSOProfileOptions defaultWebSSOProfileOptions() {
      WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions()
      webSSOProfileOptions.setForceAuthN(true)
      webSSOProfileOptions.setIncludeScoping(false)
      if (usePasswordProtectedTransport) {
        webSSOProfileOptions.setAuthnContexts(Arrays.asList("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport"))
      }
      return webSSOProfileOptions
    }

    @Bean
    SAMLEntryPoint samlEntryPoint() {
      SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint()
      samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions())
      return samlEntryPoint
    }

    @Bean
    SAMLDiscovery samlIDPDiscovery() {
      SAMLDiscovery idpDiscovery = new SAMLDiscovery()
      idpDiscovery.setIdpSelectionPath("/saml/idpSelection")
      return idpDiscovery
    }

    @Bean
    @Qualifier("ip")
    ExtendedMetadataDelegate identityProvider() throws MetadataProviderException {
      MetadataProvider provider = getMetadataProvider("/resources/saml/ip.xml")
      provider.setRequireValidMetadata(false)
      ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(provider, identityProviderExtendedMetadata())
      extendedMetadataDelegate.setMetadataTrustCheck(false)
      extendedMetadataDelegate.setMetadataRequireSignature(false)
      backgroundTaskTimer.purge()
      extendedMetadataDelegate.initialize()
      return extendedMetadataDelegate
    }

    @Bean
    ExtendedMetadata identityProviderExtendedMetadata() {
      ExtendedMetadata extendedMetadata = new ExtendedMetadata()
      extendedMetadata.setAlias("Pierce County SAML")
      extendedMetadata.setIdpDiscoveryEnabled(true)
      extendedMetadata.setSignMetadata(false)
      extendedMetadata.setEcpEnabled(true)
      return extendedMetadata
    }

    protected MetadataProvider getMetadataProvider(String classPathResource) throws MetadataProviderException {
      try {
        ClasspathResource resource = null
        resource = new ClasspathResource(classPathResource)
        DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        factory.setNamespaceAware(true)
        DocumentBuilder builder = factory.newDocumentBuilder()
        Document doc = builder.parse(resource.getInputStream())
        Element element = doc.getDocumentElement()
        MetadataProvider provider = new DOMMetadataProvider(element)
        return provider
      } catch(Exception ex) {
        throw new MetadataProviderException(ex)
      }
    }

    @Bean
    @Qualifier("sp")
    ExtendedMetadataDelegate serviceProvider() throws MetadataProviderException {
      MetadataProvider provider = getMetadataProvider("/resources/saml/sp.xml")
      provider.setRequireValidMetadata(false)
      ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(provider, serviceProviderExtendedMetadata())
      extendedMetadataDelegate.setMetadataTrustCheck(false)
      extendedMetadataDelegate.setMetadataRequireSignature(false)
      extendedMetadataDelegate.setRequireValidMetadata(false)
      backgroundTaskTimer.purge()
      extendedMetadataDelegate.initialize()
      return extendedMetadataDelegate
    }

    @Bean
    ExtendedMetadata serviceProviderExtendedMetadata() {
      ExtendedMetadata extendedMetadata = new ExtendedMetadata()
      extendedMetadata.setAlias("Pierce County SAML")
      extendedMetadata.setLocal(true)
      extendedMetadata.setIdpDiscoveryEnabled(false)
      extendedMetadata.setSignMetadata(false)
      extendedMetadata.setEcpEnabled(true)
      extendedMetadata.setRequireArtifactResolveSigned(false)
      extendedMetadata.setRequireLogoutRequestSigned(false)
      extendedMetadata.setRequireLogoutResponseSigned(false)
      return extendedMetadata
    }

    @Bean
    @Qualifier("metadata")
    CachingMetadataManager metadata() throws MetadataProviderException {
      List<MetadataProvider> providers = new ArrayList<MetadataProvider>()
      providers.add(identityProvider())
      providers.add(serviceProvider())
      CachingMetadataManager metadataManager = new CachingMetadataManager(providers)
      metadataManager.refreshMetadata()
      return metadataManager
    }

    @Bean
    MetadataGenerator metadataGenerator() {
      MetadataGenerator metadataGenerator = new MetadataGenerator()
      metadataGenerator.setEntityBaseURL("https://${Settings.serverName}${Settings.contextPath}".trim())
      // metadataGenerator.setEntityId("com:vdenotaris:spring:sp")
      // metadataGenerator.setExtendedMetadata(extendedMetadata())
      metadataGenerator.setIncludeDiscoveryExtension(false)
      // metadataGenerator.setKeyManager(keyManager())
      return metadataGenerator
    }

    @Bean
    MetadataDisplayFilter metadataDisplayFilter() {
      return new MetadataDisplayFilter()
    }

    @Bean
    SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler() {
      SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler = new SavedRequestAwareAuthenticationSuccessHandler()
      successRedirectHandler.setAlwaysUseDefaultTargetUrl(false)
      successRedirectHandler.setDefaultTargetUrl("/")
      return successRedirectHandler
    }

    @Bean
    SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
      SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler()
      failureHandler.setUseForward(true)
      failureHandler.setDefaultFailureUrl("/")
      return failureHandler
    }

    @Bean
    SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter() throws Exception {
      SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter = new SAMLWebSSOHoKProcessingFilter()
      samlWebSSOHoKProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler())
      samlWebSSOHoKProcessingFilter.setAuthenticationManager(authenticationManager())
      samlWebSSOHoKProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler())
      return samlWebSSOHoKProcessingFilter
    }

    @Bean
    SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
      SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter()
      samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager())
      samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler())
      samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler())
      return samlWebSSOProcessingFilter
    }

    @Bean
    MetadataGeneratorFilter metadataGeneratorFilter() {
      return new MetadataGeneratorFilter(metadataGenerator())
    }

    @Bean
    SimpleUrlLogoutSuccessHandler successLogoutHandler() {
      SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler()
      successLogoutHandler.setDefaultTargetUrl("/")
      return successLogoutHandler
    }

    @Bean
    SecurityContextLogoutHandler logoutHandler() {
      SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler()
      logoutHandler.setInvalidateHttpSession(true)
      logoutHandler.setClearAuthentication(true)
      return logoutHandler
    }

    @Bean
    SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
      return new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler())
    }

    @Bean
    SAMLLogoutFilter samlLogoutFilter() {
      return new SAMLLogoutFilter(
          successLogoutHandler(),
          [logoutHandler()] as LogoutHandler[],
          [logoutHandler()] as LogoutHandler[]
      )
    }

    private ArtifactResolutionProfile artifactResolutionProfile() {
      final ArtifactResolutionProfileImpl artifactResolutionProfile = new ArtifactResolutionProfileImpl(httpClient())
      artifactResolutionProfile.setProcessor(new SAMLProcessorImpl(soapBinding()))
      return artifactResolutionProfile
    }

    @Bean
    HTTPArtifactBinding artifactBinding(ParserPool parserPool, VelocityEngine velocityEngine) {
      return new HTTPArtifactBinding(parserPool, velocityEngine, artifactResolutionProfile())
    }

    @Bean
    HTTPSOAP11Binding soapBinding() {
      return new HTTPSOAP11Binding(parserPool())
    }

    @Bean
    HTTPPostBinding httpPostBinding() {
      return new HTTPPostBinding(parserPool(), velocityEngine())
    }

    @Bean
    HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
      return new HTTPRedirectDeflateBinding(parserPool())
    }

    @Bean
    HTTPSOAP11Binding httpSOAP11Binding() {
      return new HTTPSOAP11Binding(parserPool())
    }

    @Bean
    HTTPPAOS11Binding httpPAOS11Binding() {
      return new HTTPPAOS11Binding(parserPool())
    }

    @Bean
    SAMLProcessorImpl processor() {
      Collection<SAMLBinding> bindings = new ArrayList<SAMLBinding>()
      bindings.add(httpRedirectDeflateBinding())
      bindings.add(httpPostBinding())
      bindings.add(artifactBinding(parserPool(), velocityEngine()))
      bindings.add(httpSOAP11Binding())
      bindings.add(httpPAOS11Binding())
      return new SAMLProcessorImpl(bindings)
    }

    @Bean
    FilterChainProxy samlFilter() throws Exception {
      List<SecurityFilterChain> chains = new ArrayList<SecurityFilterChain>()
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"), samlEntryPoint()))
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"), samlLogoutFilter()))
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"), metadataDisplayFilter()))
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"), samlWebSSOProcessingFilter()))
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSOHoK/**"), samlWebSSOHoKProcessingFilter()))
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"), samlLogoutProcessingFilter()))
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/discovery/**"), samlIDPDiscovery()))
      return new FilterChainProxy(chains)
    }

    @Bean
    FilterRegistrationBean samlFilterRegistration() {
      FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(samlFilter())
      filterRegistrationBean.setOrder(SecurityFilterPosition.SECURITY_CONTEXT_FILTER.order + 1)
      return filterRegistrationBean
    }

    @Bean
    @Override
    AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean()
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(samlAuthenticationProvider())
    }

}
