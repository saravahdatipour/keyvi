<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=false displayMessage=false; section>
<#assign enableOption = (enableOption!false)>
    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "form">
        <#if realm.password>
            <div class="container">
                <div id="loginbox" style="margin-top:100px;" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
                    <div class="panel panel-info" >
                        <div class="panel-heading">
                            <div class="panel-title">Sign In</div>
                            <#if realm.resetPasswordAllowed>
                                <div style="float:right; font-size: 80%; position: relative; top:-10px"><a href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a></div>
                            </#if>
                        </div>

                        <div class="panel-body" >
                            <#if message?has_content>
                                <div id="login-alert" class="alert alert-danger col-sm-12">
                                    <span class="kc-feedback-text">${kcSanitize(message.summary)?no_esc}</span>
                                </div>
                            </#if>

                            <form id="kc-form-login" class="${properties.kcFormClass!}" onsubmit="login.disabled = true; return true;" action="${url.loginAction?keep_after('^[^#]*?://.*?[^/]*', 'r')}" method="post">
                                <input type="hidden" name="login_method" id="login-method" value="standard">
                                <input type="hidden" name="claims" id="claims">
                                <div class="${properties.kcInputWrapperClass!}">
                                    <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                                    <#if usernameEditDisabled??>
                                        <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}" type="text" disabled placeholder="<#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if>"/>
                                    <#else>
                                        <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}" type="text" autofocus autocomplete="off" placeholder="<#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if>" />
                                    </#if>
                                </div>

                                <div class="${properties.kcInputWrapperClass!}">
                                    <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
                                    <input tabindex="2" id="password" class="${properties.kcInputClass!}" name="password" type="password" autocomplete="off" placeholder="${msg("password")}"/>
                                </div>

                                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                                    <#if realm.rememberMe && !usernameEditDisabled??>
                                        <div class="checkbox">
                                            <label>
                                                <#if login.rememberMe??>
                                                    <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" tabindex="3" checked> ${msg("rememberMe")}
                                                <#else>
                                                    <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" tabindex="3"> ${msg("rememberMe")}
                                                </#if>
                                            </label>
                                        </div>
                                    </#if>
                                </div>

                                <div id="kc-form-buttons" style="margin-top:10px" class="${properties.kcFormButtonsClass!}">
                                    <div class="${properties.kcFormButtonsWrapperClass!}">
                                        <#--  <input tabindex="4" class="${properties.kcButtonClass!}" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>  -->
                                        <button type="submit" class="${properties.kcButtonClass!}" id="standard-login" onclick="document.getElementById('login-method').value='standard'">${msg("doLogIn")}</button>


                                        <#if realm.password && social.providers??>
                                            <#list social.providers as p>
                                                <a href="${p.loginUrl}" id="zocial-${p.alias}" class="btn btn-primary">${msg("doLogIn")} With ${p.displayName}</a>
                                            </#list>
                                        </#if>
                                         <!-- Yivi Web Form Integration -->
                                     <#if enableYivi>
                                         <button id="start-popup" type="button">Login With Yivi</button>
                                         <button type="button" class="${properties.kcButtonClass!}" id="start-popup">Login With Yivi</button>


                                     <#else>
                                         <p>Yivi is not enabled!</p>
                                     </#if>

                                     <#if countryIdentifier?has_content>
                                         ${countryIdentifier}
                                     </#if>

                            <script>
                    document.addEventListener('DOMContentLoaded', function() {
                        const startPopupButton = document.getElementById('start-popup');
                        const claimsInput = document.getElementById('claims');
                        const loginMethodInput = document.getElementById('login-method');

                        if (startPopupButton) {
                            let options = {
                                debugging: true,
                                language: 'en',
                                translations: {
                                    header: 'Try this <i class="yivi-web-logo">Yivi</i> example',
                                    loading: 'Just one second please!'
                                },
                                session: {
                                    url: 'https://159.65.93.73:8088',
                                    start: {
                                        method: 'POST',
                                        headers: {
                                            'Content-Type': 'application/json'
                                        },
                                        body: JSON.stringify({
                                            "@context": "https://irma.app/ld/request/disclosure/v2",
                                            "disclose": ${identifiersStringified?no_esc}
                                        })
                                    },
                                }
                            };

                            let yiviPopup = window.yivi.newPopup(options);

                            startPopupButton.onclick = () => {
                                loginMethodInput.value = 'yivi';
                                yiviPopup.start()
                                    .then(result => {
                                        claimsInput.value = JSON.stringify(result);
                                        document.getElementById('kc-form-login').submit();
                                    })
                                    .catch(error => {
                                        if (error === 'Aborted') {
                                            console.log('We closed it ourselves, so no problem 😅');
                                            return;
                                        }
                                        console.error("Couldn't do what you asked 😢", error);
                                    })
                                    .finally(() => yiviPopup = window.yivi.newPopup(options));
                            };
                        }
                    });
</script>



                                    </div>
                                </div>

                                <#if realm.password && realm.registrationAllowed && !usernameEditDisabled??>
                                    <div class="form-group">
                                        <div class="col-md-12 control">
                                            <div style="border-top: 1px solid#888; padding-top:15px;" >
                                                ${msg("noAccount")}
                                                <a tabindex="6" href="${url.registrationUrl}" style="font-weight: bold;">
                                                    ${msg("doRegister")}
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </#if>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </#if>
    </#if>
</@layout.registrationLayout>