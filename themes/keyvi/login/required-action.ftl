<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=false displayMessage=false; section>
    <#assign enableYivi = true> 
    <#if section = "form">
        <div id="loginbox" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
            <div class="panel panel-info">
                <div class="panel-body">
                    <!-- Yivi Web Form Integration -->
                    <#if enableYivi>
                        <p>One more step!</p>
                        <button id="start-popup" type="button">Login With Yivi</button>
                    <#else>
                        <p>Yivi is not enabled!</p>
                    </#if>

                    <script>
                        document.addEventListener('DOMContentLoaded', function() {
                            const startPopupButton = document.getElementById('start-popup');
                            if (startPopupButton) {
                                let options = {
                                    debugging: false,
                                    language: 'en',
                                    translations: {
                                        header: 'Try this <i class="yivi-web-logo">Yivi</i> example',
                                        loading: 'Just one second please!'
                                    },
                                    session: {
                                        url: 'https://catchthebugs.com',
                                        start: {
                                            method: 'POST',
                                            headers: {
                                                'Content-Type': 'application/json'
                                            },
                                            body: JSON.stringify({
                                                "@context": "https://irma.app/ld/request/disclosure/v2",
                                                "disclose": [
                                                    [
                                                        [
                                                            { "type": "irma-demo.MijnOverheid.ageLower.over18", "value": "yes" }
                                                        ]
                                                    ]
                                                ]
                                            })
                                        },
                                    }
                                };

                                let yiviPopup = window.yivi.newPopup(options);

                                startPopupButton.onclick = () => {
                                    yiviPopup.start()
                                        .then(result => {
                                            console.log("Authentication successful", result);
                                        })
                                        .catch(error => {
                                            console.error("Error during authentication", error);
                                        });
                                };
                            }
                        });
                    </script>
                </div>
            </div>
        </div>
    </#if>
</@layout.registrationLayout>
