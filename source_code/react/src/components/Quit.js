import React from 'react';
import $ from 'jquery';
import { Form, Button, message, Table } from 'antd';
import { API_ROOT } from '../constants';
import { Link } from 'react-router-dom';
import {Log} from './Log';
import {LogData} from './LogData';
import {ResponseLogData} from './ResponseLogData';

const FormItem = Form.Item;

class QuitForm extends React.Component {
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
                    data: "quit"
                }).then(function(response) {
                    message.success("simulation memory cleaned");
                    parent.state.reportData = response;
                    //this.state.reportData = response;

                    this.state = parent.state;
                    parent.state.reportData = response;
                    LogData.deleteData();
                    ResponseLogData.deleteData();
                    var d = LogData.getData();
                    var rld = ResponseLogData.getData();
                    parent.props.transferMsg(d, rld);

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
                    span: 14,
                    offset: 0,
                },
                sm: {
                    span: 12,
                    offset: 4,
                },
            },
        };

        return (
            <Form onSubmit={this.handleSubmit} className="singlesim-form">

                <FormItem {...tailFormItemLayout}>
                    <Button type="primary" htmlType="submit">Stop Run</Button>
                </FormItem>
                <p>{this.state.reportData}</p>

            </Form>

        );
    }
}

export const Quit = Form.create()(QuitForm);