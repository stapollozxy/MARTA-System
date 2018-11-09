import React from 'react';
import $ from 'jquery';
import { Form, Button, message, Table } from 'antd';
import { API_ROOT } from '../constants';
import { Link } from 'react-router-dom';
import {Log} from './Log';
import {LogData} from './LogData';
import {ResponseLogData} from './ResponseLogData';

const FormItem = Form.Item;

class StringReportForm extends React.Component {
    constructor(){
        super();
        this.state ={
            reportData: ''
        };
    }

    state = {
        confirmDirty: false,
        autoCompleteResult: [],
        reportData: 'sd',
    };


    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);
                //var data = new LogData();
                var parent = this;
               // this.setState({reportData : '233'});
                $.ajax({
                    url: `${API_ROOT}/command`,
                    method: 'POST',
                    data: "system_report"
                }).then(function(response) {
                    message.success("system reporting");
                    parent.state.reportData = response;
                    //this.state.reportData = response;

                    this.state = parent.state;
                    parent.state.reportData = response;
                    parent.setState({reportData : parent.state.reportData});
                    console.log(this.state.reportData);

                }, function(response) {
                    message.error(response.responseText);
                }).catch(function(error) {
                    message.error(error);
                });
                //this.setState({reportData : parent.state.reportData});
            }
        });
    }

    checkConfirm = (rule, value, callback) => {
        const form = this.props.form;
        if (value && this.state.confirmDirty) {
            form.validateFields(['confirm'], { force: true });
        }
        callback();
    }

    render() {
        const { getFieldDecorator } = this.props.form;

        const tailFormItemLayout = {
            wrapperCol: {
                xs: {
                    span: 24,
                    offset: 0,
                },
                sm: {
                    span: 16,
                    offset: 8,
                },
            },
        };

        return (
            <Form onSubmit={this.handleSubmit} className="singlesim-form">

                <FormItem {...tailFormItemLayout}>
                    <Button type="primary" htmlType="submit">Report</Button>
                </FormItem>
                <p align="left">{this.state.reportData}</p>

            </Form>

        );
    }
}

export const SysReport = Form.create()(StringReportForm);